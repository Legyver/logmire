package com.legyver.logmire.ui.search;

import com.legyver.logmire.task.search.SearchTaskContext;
import com.legyver.logmire.ui.bean.LogLineUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;

public class InMemoryIndex {
	private static final Logger logger = LogManager.getLogger(InMemoryIndex.class);

	private final Map<String, GraphNode> indexMap = new HashMap<>();
	/**
	 * If the user has entered a long search text than any index, we can depth-first search these for a match with pruning.
	 */
	private final Map<String, GraphNode> partialMatchTreeMap = new HashMap<>();

	private boolean debugIndexing = false;
	private boolean debugIndexDemotions = debugIndexing && false;
	private boolean debugIndexPartials = debugIndexing && false;
	private boolean debugSearch = true;
	private boolean debugSearchPruning = debugSearch && true;
	private boolean debugSearchAbortion = debugSearch && true;
	private boolean debugSearchPartials = debugSearch && true;
	private boolean debugSearchResultSort = debugSearch && false;

	public void index(String indexPattern, LogLineUI logLineUI) {
		//we always add to root index
		GraphNode graphNode = indexMap.get(indexPattern);
		if (graphNode == null) {
			synchronized (this) {
				graphNode = indexMap.computeIfAbsent(indexPattern, (key) -> new GraphNode());
			}
			if (logger.isTraceEnabled() && debugIndexing) {
				logger.trace("Adding root index: " + indexPattern);
			}
		}
		synchronized (graphNode) {
			graphNode.lines.put(logLineUI.getEntryNumber(), logLineUI);
		}
		if (logger.isTraceEnabled() && debugIndexing) {
			logger.trace("Adding line to index [" + indexPattern + "]: " + logLineUI.getLongMessage());
		}

		GraphNode childNode = graphNode;
		String childPattern = indexPattern;
		//also register parent if applicable to help with search pruning
		for (int len = indexPattern.length() - 1; len > 0; len--) {
			for (int len2 = 0; len2 < len; len2++) {
				String parentPattern = indexPattern.substring(len2, len).toLowerCase().trim();

				GraphNode parentNode = indexMap.get(parentPattern);
				if (parentNode == null) {
					synchronized (this) {
						parentNode = indexMap.computeIfAbsent(parentPattern, (key) -> new GraphNode());
					}
					if (logger.isTraceEnabled() && debugIndexPartials) {
						logger.trace("Adding root index for parent: " + parentPattern);
					}
				}

				//we use a map for below because we want to prune insertions as well
				if (parentNode.lines.containsKey(logLineUI.getEntryNumber())) {
					break;
				} else {
					synchronized (parentNode) {
						if (!parentNode.lines.containsKey(logLineUI.getEntryNumber())) {
							parentNode.lines.put(logLineUI.getEntryNumber(), logLineUI);
							if (logger.isTraceEnabled() && debugIndexPartials) {
								logger.trace("Adding line to index [" + parentPattern + "]: " + logLineUI.getLongMessage());
							}
						}
						childNode.parent = parentNode;
						parentNode.children.putIfAbsent(childPattern, childNode);
					}
					//demote the child if it's top-level in the partial map
					synchronized (partialMatchTreeMap) {
						partialMatchTreeMap.remove(childPattern);
						partialMatchTreeMap.putIfAbsent(parentPattern, parentNode);
					}
					if (logger.isTraceEnabled() && debugIndexDemotions) {
						logger.trace("Demoting node [" + childPattern + "] to: " + parentPattern);
					}
					childNode = parentNode;
					childPattern = parentPattern;
				}
			}
		}
	}

	/**
	 * This method is private because we don't want it to be called directly.
	 * It should only be invoked via the SearchTaskContext
	 * @param searchCtx
	 * @return
	 */
	private List<LogLineUI> search(SearchCtx searchCtx) {
		List<LogLineUI> results;

		if (indexMap.containsKey(searchCtx.searchLC)) {
			if (logger.isTraceEnabled() && debugSearch) {
				logger.trace("Exact match found for index: " + searchCtx.searchLC);
			}
			GraphNode node = indexMap.get(searchCtx.searchLC);
			results = new ArrayList<>(node.lines.values());
		} else {
			if (logger.isTraceEnabled() && debugSearch) {
				logger.trace("No exact match found for index: " + searchCtx.searchLC);
			}
			///long search
			Map<Integer, LogLineUI> resultMap = new HashMap<>();
			for (Iterator<String> it = partialMatchTreeMap.keySet().iterator(); it.hasNext();) {
				String indexString = it.next();
				if (logger.isTraceEnabled() && debugSearchPartials) {
					logger.trace("Checking: " + indexString);
				}
				GraphNode graphNode = partialMatchTreeMap.get(indexString);
				if (searchCtx.isPruned(graphNode)) {
					if (logger.isTraceEnabled() && debugSearchPruning) {
						logger.trace("Found pruned search: " + indexString);
					}
				} else {
					if (logger.isTraceEnabled() && debugSearchPruning) {
						logger.trace("Optimistically pruning search: " + indexString);
					}
					searchCtx.prune(graphNode);

					Collection<LogLineUI> indexedLines = graphNode.lines.values();
					for (Iterator<LogLineUI> logLineUIIterator = indexedLines.iterator(); logLineUIIterator.hasNext();) {
						LogLineUI logLineUI = logLineUIIterator.next();
						String longMessageLC = logLineUI.getLongMessage().toLowerCase(Locale.getDefault()).trim();
						if (longMessageLC.contains(searchCtx.searchLC)) {
							if (logger.isTraceEnabled() && debugSearchPartials) {
								logger.trace("Adding partial match [" + indexString + "]: " + longMessageLC);
							}
							resultMap.putIfAbsent(logLineUI.getEntryNumber(), logLineUI);
						}
						if (searchCtx.isCancelled()) {
							if (logger.isTraceEnabled() && debugSearchAbortion) {
								logger.trace("Aborting search in inner loop: " + searchCtx.searchLC);
							}
							break;
						}
					}
				}
				if (searchCtx.isCancelled()) {
					if (logger.isTraceEnabled() && debugSearchAbortion) {
						logger.trace("Aborting search at outer loop: " + searchCtx.searchLC);
					}
					break;
				}
			}
			results = new ArrayList<>(resultMap.values());
		}
		if (logger.isTraceEnabled() && debugSearch) {
			logger.trace("Search [" + searchCtx.searchLC + "] returned " + results.size() + " values");
		}

		if (logger.isTraceEnabled() && debugSearchResultSort) {
			logger.trace("About to sort results for: " + searchCtx.searchLC);
		}
		Collections.sort(results);
		if (logger.isTraceEnabled() && debugSearchResultSort) {
			logger.trace("Completed sorting results for: " + searchCtx.searchLC);
		}

		return results;
	}

	public void reset() {
		indexMap.clear();
	}

	public SearchTaskContext searchTaskContext(String currentSearch) {
		return new SearchTaskContext(partialMatchTreeMap.size(), (predicate) -> search(new SearchCtx(currentSearch, predicate)));
	}

	private static class GraphNode {
		UUID uuid = UUID.randomUUID();
		Map<String, GraphNode> children = new HashMap<>();
		Map<Integer, LogLineUI> lines = new HashMap<>();
		GraphNode parent;
	}

	private static class SearchCtx {
		private final String searchLC;
		private final Predicate cancelSearch;
		private final Set<UUID> prunedNodes = new HashSet<>();

		public SearchCtx(String searchPattern, Predicate cancelSearch) {
			this.searchLC = searchPattern.toLowerCase(Locale.getDefault()).trim();
			this.cancelSearch = cancelSearch;
		}

		boolean isPruned(GraphNode graphNode) {
			if (prunedNodes.contains(graphNode.uuid)) {
				return true;
			}
			if (graphNode.parent != null) {
				return isPruned(graphNode.parent);
			}
			return false;
		}

		void prune(GraphNode graphNode) {
			prunedNodes.add(graphNode.uuid);
		}

		boolean isCancelled() {
			return cancelSearch.test(this);
		}
	}

}
