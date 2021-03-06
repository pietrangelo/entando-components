/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.agiletec.plugins.jpcrowdsourcing.aps.tags;

import com.agiletec.aps.tags.InternalServletTag;

public class IdeaInstanceIntroTag extends InternalServletTag {
	
	@Override
	public void release() {
		super.release();
		this.setActionName(null);
	}
	
	@Override
	public String getActionPath() {
		String defaultActionName = "list";
		String actionPath = null;
		if (null != this.getActionName() && this.getActionName().equalsIgnoreCase("search_result")) {
			actionPath = "/ExtStr2/do/collaboration/FrontEnd/Idea/Manage/introSearchResult.action";
		} else {
			actionPath = "/ExtStr2/do/collaboration/FrontEnd/Idea/Manage/" + defaultActionName + ".action";
		}
		return actionPath;
	}
	
	public String getActionName() {
		return _actionName;
	}
	public void setActionName(String actionName) {
		this._actionName = actionName;
	}

	private String _actionName;
}


