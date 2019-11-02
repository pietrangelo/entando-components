/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.plugins.jacms.aps.system.services.content;

import java.util.Collection;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;

/**
 * Interfaccia base per i Data Access Object 
 * riservato al caricamento/ricerca lista di contenuti nell'area di lavoro.
 * @author E.Santoboni
 */
public interface IWorkContentSearcherDAO extends IContentSearcherDAO {
	
	/**
	 * Carica una lista di identificativi di contenuti in base ai parametri immessi.
	 * @param filters L'insieme dei filtri sugli attibuti, su cui la ricerca deve essere effettuata.
	 * @param userGroupCodes I codici dei gruppi utenti dell'utente richiedente la lista. 
	 * Se la collezione è vuota o nulla, gli identificativi di contenuto erogati saranno 
	 * relativi al gruppo definito "ad accesso libero". Nel caso nella collezione sia presente 
	 * il codice del gruppo degli amministratori, non sarà applicato alcun il filtro sul gruppo.
	 * @return La lista degli id dei contenuti cercati.
	 */
	public List<String> loadContentsId(EntitySearchFilter[] filters, Collection<String> userGroupCodes);
        
	public List<String> loadContentsId(String[] categories, EntitySearchFilter[] filters, Collection<String> userGroupCodes);
	
}
