/*
 * Copyright (C) 2012-2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.client.exception.PermissionDeniedExceptionClient;
import org.opendatakit.aggregate.client.exception.RequestFailureException;
import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.odktables.entity.serialization.OdkTablesKeyValueManifestManager;
import org.opendatakit.common.persistence.client.exception.DatastoreFailureException;
import org.opendatakit.common.security.client.exception.AccessDeniedException;
import org.opendatakit.common.web.CallingContext;
import org.opendatakit.common.web.constants.HtmlConsts;

/**
 * Handles the request for a
 * Based on XFormsManifestServlet.
 * @author sudars
 *
 */
public class OdkTablesManifestServlet extends ServletUtilBase {

	/**
	 *
	 */
	private static final long serialVersionUID = -6054191018860216729L;

	/**
	 * URI from base
	 */
	public static final String ADDR = "tableKeyValueManifest";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CallingContext cc = ContextFactory.getCallingContext(this, req);

		// reclaim the parameters, which in this case is just the
		// tableId
		String tableId = getParameter(req, ServletConsts.TABLE_ID);
		if (tableId == null) {
			errorMissingKeyParam(resp);
			return;
		}

		OdkTablesKeyValueManifestManager mm = new OdkTablesKeyValueManifestManager(tableId, cc);

		String manifest;
		try {
			manifest = mm.getManifest();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			errorRetreivingData(resp);
			return;
		} catch (RequestFailureException e) {
			e.printStackTrace();
			errorRetreivingData(resp);
			return;
		} catch (PermissionDeniedExceptionClient e) {
		  e.printStackTrace();
		  errorRetreivingData(resp);
		  return;
		} catch (DatastoreFailureException e) {
			e.printStackTrace();
			datastoreError(resp);
			return;
		}

		resp.setContentType(HtmlConsts.RESP_TYPE_JSON);
		resp.getWriter().write(manifest);


	}

}