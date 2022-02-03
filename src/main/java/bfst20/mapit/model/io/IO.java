package bfst20.mapit.model.io;

import java.io.File;
import java.util.List;
import bfst20.mapit.Model;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import bfst20.mapit.model.DAWAAddress;
import bfst20.mapit.model.AutoComplete;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.FactoryConfigurationError;

@SuppressWarnings("unchecked")
public class IO {
	private Model model;
	private long totalLoadingTime = 0;

	public IO(Model model) {
		this.model = model;
	}

	public void load(File file) throws IOException, XMLStreamException, FactoryConfigurationError {
		long time = -System.nanoTime();
		String filename = file.getName();
		String fileExt = filename.substring(filename.lastIndexOf("."));
		switch (fileExt) {
			case ".bin":
				model.loadBinary(file);
				break;
			case ".osm":
				model.loadOSM(file);
				break;
		}
		time += System.nanoTime() + totalLoadingTime;
		System.out.printf("Load time: %.3fms\n", time / 1e6);
		model.notifyObservers();
		totalLoadingTime = 0;
	}

	public void save(File file) throws IOException {
		long time = -System.nanoTime();
		if (file.getName().endsWith(".bin")) {
			try (var out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
				SavedFile savedFile = new SavedFile();
				savedFile.setTrees(model.getRTrees());
				savedFile.setBounds(model.bounds);
				savedFile.setGraph(model.getGraph());
				savedFile.setIdToIndexHashMap(model.getIdToIndexHashMap());
				savedFile.setIdToNode(model.getIdToNodeList());
				savedFile.setWayIdToRoadname(model.getWayIdToRoadname());

				out.writeObject(savedFile);
			}
		} else {
			System.out.println("Something went wrong");
		}

		time += System.nanoTime();
		System.out.printf("Save time: %.3fms\n", time / 1e6);
	}

	public List<AutoComplete> loadAddresses() {
		long time = -System.nanoTime();

		try (var inputStream = getClass().getClassLoader().getResourceAsStream("data/addresses.bin")) {
            ObjectInputStream obj = new ObjectInputStream(inputStream);
            List dawaAddresses = (List<DAWAAddress>) obj.readObject();
			time += System.nanoTime();
			System.out.printf("Addresses loaded in: %.3fms\n", time / 1e6);
			totalLoadingTime += time;
			return dawaAddresses;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}