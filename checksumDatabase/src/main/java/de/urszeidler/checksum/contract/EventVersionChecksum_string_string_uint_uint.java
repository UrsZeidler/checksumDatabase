package de.urszeidler.checksum.contract;

import de.urszeidler.checksum.contract.ChecksumDatabase.*;

/**
 * The Event Objects for the event VersionChecksum(String version,String checksum,Integer date,Integer id).
 *
 */
public class EventVersionChecksum_string_string_uint_uint{
	private String version;
	private String checksum;
	private Integer date;
	private Integer id;

	public EventVersionChecksum_string_string_uint_uint(String version,String checksum,Integer date,Integer id) {
		super();
		this.version = version;
		this.checksum = checksum;
		this.date = date;
		this.id = id;
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

	/**
	 * Getter for id.
	 * @return
	 */
	public Integer getId(){
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EventVersionChecksum_string_string_uint_uint other = (EventVersionChecksum_string_string_uint_uint) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventVersionChecksum_string_string_uint_uint [version=" + version + ",checksum=" + checksum + ",date=" + date + ",id=" + id + "]";
	}
}
