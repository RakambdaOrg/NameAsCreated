package fr.mrcraftcod.nameascreated.extractor;

import com.drew.metadata.Directory;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-09-30
 */
public class SimpleDateExtractor<T extends Directory> implements DateExtractor<T>{
	private final int tag;
	private final Class<T> klass;
	
	public SimpleDateExtractor(final Class<T> klass, final int tag){
		this.klass = klass;
		this.tag = tag;
	}
	
	@Override
	public Date parse(final Directory directory, final TimeZone tz)
	{
		return directory.getDate(this.tag, tz);
	}
	
	public Class<T> getKlass(){
		return klass;
	}
}
