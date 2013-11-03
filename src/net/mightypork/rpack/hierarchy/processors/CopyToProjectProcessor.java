package net.mightypork.rpack.hierarchy.processors;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;


public class CopyToProjectProcessor implements AssetTreeProcessor {

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private List<String> ignoredSources = new ArrayList<String>();


	public CopyToProjectProcessor() {

	}


	public void addIgnoredSource(String source) {

		ignoredSources.add(source);
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return; // no double-processing
		processed.add(node);

		if (node instanceof AssetTreeGroup) {

			return;

		} else if (node instanceof AssetTreeLeaf) {

			File targetBase = Projects.getActive().getAssetsBaseDirectory();

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;
			String source = leaf.resolveAssetSource();
			String sourceMeta = leaf.resolveAssetMetaSource();

			if (ignoredSources.contains(source)) return;

			// prepare files
			String path = leaf.getAssetEntry().getPath();
			File target = new File(targetBase, path);
			File tgDir = target.getParentFile();
			tgDir.mkdirs();
			File targetMeta = new File(targetBase, path + ".mcmeta");

			// if source resolved to project, no work here.

			if (!MagicSources.isProject(sourceMeta)) {
				try {
					InputStream in;
					OutputStream out;

					in = Sources.getAssetMetaStream(sourceMeta, leaf.getAssetKey());
					if (in != null) {
						out = new FileOutputStream(targetMeta);
						FileUtils.copyStream(in, out);
					}

				} catch (Exception e) {
					Log.e("Error copying asset meta to project folder.", e);
				}
			}


			if (!MagicSources.isProject(source)) {

				try {
					InputStream in;
					OutputStream out;

					in = Sources.getAssetStream(source, leaf.getAssetKey());
					out = new FileOutputStream(target);
					FileUtils.copyStream(in, out);

				} catch (Exception e) {
					Log.e("Error copying asset to project folder.", e);
				}
			}
		}
	}

}
