package net.mightypork.rpack.hierarchy.processors;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.utils.Log;


public class DeleteFromProjectProcessor implements AssetTreeProcessor {

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private boolean assets = true, meta = true;


	public DeleteFromProjectProcessor() {

	}


	public DeleteFromProjectProcessor(boolean assets, boolean meta) {

		this.assets = assets;
		this.meta = meta;
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return;
		processed.add(node);

		if (assets && MagicSources.isProject(node.getLibrarySource())) {
			node.setLibrarySource(MagicSources.INHERIT);
		}

		if (node instanceof AssetTreeGroup) {


			return;

		} else {

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;

			if (!Projects.getActive().doesProvideAsset(leaf.getAssetKey())) {
				return; // not in project
			}

			String path = leaf.getAssetEntry().getPath();

			File base = Projects.getActive().getAssetsBaseDirectory();
			File target = new File(base, path);
			File targetMeta = new File(base, path + ".mcmeta");

			Log.f3("Deleting: " + target);
			if (assets) target.delete();
			if (meta) targetMeta.delete();
		}
	}

}
