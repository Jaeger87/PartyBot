


public enum CallBackCodes {
	
	PHOTOYES("PY"),PHOTONO("PN"),MUSICYES("MY"),MUSICNO("MN");
	
	
	private String str;
	/**
	 * Costruttore privato che costruisce l'enum da stringa
	 * @param str
	 */
	private CallBackCodes(String str)
	{
		this.str=str;
	}
	
	@Override
	public String toString()
	{
		return str;
	}
	/**
	 * Metodo per poter costruire l'enum da Stringa
	 * @param text
	 * @return
	 */
	public static CallBackCodes fromString(String text) {
		  if (text != null) {
		    for (CallBackCodes c : CallBackCodes.values()) {
		      if (text.equalsIgnoreCase(c.str)) {
		        return c;
		      }
		    }
		  }
		  return null;
		}
}
