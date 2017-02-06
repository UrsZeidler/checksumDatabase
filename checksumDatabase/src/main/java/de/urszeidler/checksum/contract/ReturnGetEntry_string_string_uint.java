package de.urszeidler.checksum.contract;

import de.urszeidler.checksum.contract.ChecksumDatabase.*;

/**
 * The return value for the function getEntry(Integer id).
 *
 * see {@link ChecksumDatabase#getEntry( Integer)}
 */
public class ReturnGetEntry_string_string_uint{
	private String _version;
	private String _checksum;
	private Integer _date;

	public ReturnGetEntry_string_string_uint(String _version,String _checksum,Integer _date) {
		super();
		this._version = _version;
		this._checksum = _checksum;
		this._date = _date;
	}

	/**
	 * Getter for _version.
	 * @return
	 */
	public String get_version(){
		return _version;
	}

	/**
	 * Getter for _checksum.
	 * @return
	 */
	public String get_checksum(){
		return _checksum;
	}

	/**
	 * Getter for _date.
	 * @return
	 */
	public Integer get_date(){
		return _date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_version == null) ? 0 : _version.hashCode());
		result = prime * result + ((_checksum == null) ? 0 : _checksum.hashCode());
		result = prime * result + ((_date == null) ? 0 : _date.hashCode());
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
		ReturnGetEntry_string_string_uint other = (ReturnGetEntry_string_string_uint) obj;
		if (_version == null) {
			if (other._version != null)
				return false;
		} else if (!_version.equals(other._version))
			return false;
		if (_checksum == null) {
			if (other._checksum != null)
				return false;
		} else if (!_checksum.equals(other._checksum))
			return false;
		if (_date == null) {
			if (other._date != null)
				return false;
		} else if (!_date.equals(other._date))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReturnGetEntry_string_string_uint [_version=" + _version + ",_checksum=" + _checksum + ",_date=" + _date + "]";
	}
}
