/**
 * 
 */
package de.urszeidler.checksum.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.adridadou.ethereum.propeller.values.EthAddress;

/**
 * @author urszeidler
 *
 */
@FacesConverter(forClass = EthAddress.class)
public class EthAddressConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return EthAddress.of(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ((EthAddress)value).withLeading0x();
	}
}
