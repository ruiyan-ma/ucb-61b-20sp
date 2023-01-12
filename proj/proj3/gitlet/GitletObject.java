package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class is the abstract class of bolbs and commits.
 * It's responsible for creating dirs and files, reading and writing
 * operations.
 *
 * @author ryan ma
 */

abstract class GitletObject implements Serializable {

	/**
	 * An empty constructor function.
	 */
	GitletObject() {

	}

	/**
	 * Return the sha1 code of this object.
	 */
	abstract String getUID();

	/**
	 * Reads in and deserializes a bolb or a commit with its UID.
	 * Return the GitletObject.
	 * Use cast to cast GitletObject to the corresponding subclass.
	 */
	static GitletObject readFromFile(String uid) {
		ObjectDir objectDir = new ObjectDir();
		File path = objectDir.getPathOfFile(uid);
		return Utils.readObject(path, GitletObject.class);
	}

	/**
	 * Write file for bolb and commit objects.
	 * Since bolb and commit only create new files and write
	 * objects into files, they don't change any file, so if
	 * this UID has already exists, do nothing. Only write when
	 * this UID doesn't exist.
	 */
	void save() throws IOException {
		String uid = getUID();
		File dir = Utils.join(ObjectDir.OBJECTS_FOLDER, uid.substring(0, 2));
		File file = Utils.join(dir, uid.substring(2));
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!file.exists()) {
			file.createNewFile();
			Utils.writeObject(file, this);
		}
	}

}
