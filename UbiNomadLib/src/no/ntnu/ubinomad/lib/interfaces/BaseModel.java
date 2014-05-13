package no.ntnu.ubinomad.lib.interfaces;

import java.util.List;

import android.content.Context;
import android.os.RemoteException;

public interface BaseModel<T> {
	public abstract long getId();
	
	public boolean save();
	public boolean update();
	public boolean delete();
	public T getById(long id);
	
	public List<T> getAll();

}
