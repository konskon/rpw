package net.mightypork.rpack.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Sources;


public class ApplyInheritProcessor implements AssetTreeProcessor {

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private String defaultSource;


	public ApplyInheritProcessor() {

		this.defaultSource = MagicSources.VANILLA;
	}


	public ApplyInheritProcessor(String defaultSource) {

		this.defaultSource = defaultSource;
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return;
		processed.add(node);

		if (!node.isLeaf()) return; // leave groups alone

		AssetTreeLeaf leaf = (AssetTreeLeaf) node;

		String assigned = leaf.getLibrarySource();
		String resolved = leaf.resolveAssetSource();

		if (!assigned.equals(resolved)) {

			if (MagicSources.isInherit(resolved) || MagicSources.isVanilla(resolved)) {
				if (MagicSources.isInherit(defaultSource) || Sources.doesSourceProvideAsset(defaultSource, leaf.getAssetEntry())) {
					resolved = defaultSource;
				}
			}

			node.setLibrarySource(resolved);
		}
	}

}
