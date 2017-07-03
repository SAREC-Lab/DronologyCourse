package edu.nd.dronology.services.supervisor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.CodeSource;

import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class WorkspaceInitializer {

	private static final ILogger LOGGER = LoggerProvider.getLogger(WorkspaceInitializer.class);
	private String root;
	private static WorkspaceInitializer instance = new WorkspaceInitializer();

	private void prepareRoot() throws DronologyServiceException {
		// if (root == null) {
		// root =
		// GlobalConfReader.getGlobalProperty(DistributorConstants.GLB_PROP_SERVER_ROOT);
		// }
		if (root == null) {
			root = getDefaultRootFolder();
		}
		if (root == null) {
			root = DronologyConstants.DEFAULT_ROOT_FOLDER;
		}
		
		root = root.replace("file:", "");
		LOGGER.info("Server workspace root location is: '" + root + "'");

		File f = new File(root);
		LOGGER.info("Absolute path is: '" + f.getAbsolutePath().toString() + "'");
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			root = f.getCanonicalPath();
		} catch (IOException e) {
			throw new DronologyServiceException("Error when setting workspace root '" + root + "'");
		}
	}

	private String getDefaultRootFolder() throws DronologyServiceException {
		CodeSource codeSource = WorkspaceInitializer.class.getProtectionDomain().getCodeSource();
		File codeFolder;
		codeFolder = new File(codeSource.getLocation().toExternalForm());
		File parent = codeFolder.getParentFile();
		if (parent == null) {
			return null;
		}
		File ws = parent.getParentFile();
		if (ws == null) {
			return null;
		}

		return Paths.get(ws.getPath(), DronologyConstants.DRONOLOGY_ROOT_FOLDER).toString();
	}

	public void prepareServerWorkspace(String workspace) throws DronologyServiceException {
		this.root = formatPath(workspace);
		prepareRoot();
		prepareFlightPathWorkspace();
		prepareSpecificationWorkspace();
	}

	private String formatPath(String workspace) {
		if (workspace == null) {
			return null;
		}
		String formated = workspace.replace("/", "\\");
		return formated;
	}

	private void prepareFlightPathWorkspace() {
		String folderPath = getFlightRouteLocation();
		File f = new File(folderPath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	private void prepareSpecificationWorkspace() {
		String folderPath = getDroneSpecificationLocation();
		File f = new File(folderPath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	
	

	String getWorkspaceLocation() {
		return root;
	}

	String getFlightRouteLocation() {
		return Paths.get(root, DronologyConstants.FOLDER_FLIGHTROUTE).toString();
	}
	
	public String getSimScenarioLocation() {
		return Paths.get(root, DronologyConstants.FOLDERN_SIM_SCENARIO).toString();
	}
	
	public String getDroneSpecificationLocation() {
		return Paths.get(root, DronologyConstants.FOLDER_SPECIFICATION).toString();
	}


	public static WorkspaceInitializer getInstance() {
		return instance;
	}

	public boolean importItem(String fileName, byte[] byteArray, boolean overwrite) throws DronologyServiceException {
		String ext = FileUtil.getExtension(fileName);
		if (ext == null) {
			LOGGER.warn("File with no extension found '" + fileName + "'");
			return false;
		}
		switch (ext) {
		case DronologyConstants.EXTENSION_FLIGHTROUTE:
			return importFlightPath(fileName, byteArray, overwrite);
		default:
			LOGGER.warn("File with extension '" + FileUtil.getExtension(fileName) + "' not processable");
			return false;
		}
	}

	private boolean importFlightPath(String fileName, byte[] content, boolean overwrite) {
		String location = getFlightRouteLocation();
		String fName = Paths.get(location, fileName).toString();
		return importFile(fName, content, overwrite);

	}

	private boolean importFile(String absolutePath, byte[] content, boolean overwrite) {
		File f = new File(absolutePath);
		if (f.exists() && !overwrite) {
			return false;
		}
		return FileUtil.saveByteArrayToFile(f, content);
	}




}
