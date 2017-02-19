package de.urszeidler.checksum.contract;

import de.urszeidler.checksum.contract.ChecksumDatabase.*;

/**
 * The dataholder for the struct ChecksumDatabaseChecksumEntry.
 *
 */
public class ChecksumDatabaseChecksumEntry{
	private String version;
	private String checksum;
	private Integer date;

	public ChecksumDatabaseChecksumEntry(String version,String checksum,Integer date) {
		super();
		this.version = version;
		this.checksum = checksum;
		this.date = date;
	}

	/**
	 * Getter for version.
	 * @return
	 */
	public String getVersion(){
		return version;
	}

	/**
	 * Getter for checksum.
	 * @return
	 */
	public String getChecksum(){
		return checksum;
	}

	/**
	 * Getter for date.
	 * @return
	 */
	public Integer getDate(){
		return date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChecksumDatabaseChecksumEntry other = (ChecksumDatabaseChecksumEntry) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (checksum == null) {
			if (other.checksum != null)
				return false;
		} else if (!checksum.equals(other.checksum))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChecksumDatabaseChecksumEntry [version=" + version + ",checksum=" + checksum + ",date=" + date + "]";
	}
}
