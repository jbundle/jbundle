/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.xml"]){
dojo._hasResource["tourapp.xml"] = true;
dojo.provide("tourapp.xml");

dojo.require("dojox.data.dom");

/**
 * Public Utilities.
 */
tourapp.xml = {
	doXSLT: function(domToBeTransformed, xsltURI, domToAppendTo, handler)
	{
		new tourapp.xml.InsertTransformTo(domToBeTransformed, xsltURI, domToAppendTo, handler);
	},
	InsertTransformTo: function(domToBeTransformed, xslUrl, elementToInsert, handler) {
		  this.xslUrl = xslUrl;
		  this.handler = handler;
		  this.xmlLoaded = this.xslLoaded = false;
		  this.elementToInsert = elementToInsert;
//		  this.load(xmlUrl, 'xml');
		  this.xml = domToBeTransformed;
		  this.xmlLoaded = true;
		  this.load(xslUrl, 'xsl');
		}

};

	tourapp.xml.InsertTransformTo.prototype.load = function (url, propertyName) {
	  var httpRequest = null;
	  if (typeof XMLHttpRequest != 'undefined') {
	    httpRequest = new XMLHttpRequest();
	  }
	  else if (typeof ActiveXObject != 'undefined') {
	    try {
	      httpRequest = new ActiveXObject('Msxml2.XMLHTTP.3.0');
	    }
	    catch (e) {}
	  }
	  if (httpRequest != null) {
	    var thisObject = this;
	    httpRequest.open('GET', url, true);
	    httpRequest.onreadystatechange = function () {
	      if (httpRequest.readyState == 4) {
	        thisObject[propertyName + 'Loaded'] = true;
	        thisObject[propertyName] = httpRequest.responseXML;
	        if (thisObject.xmlLoaded && thisObject.xslLoaded) {
	          thisObject.transformAndInsert();
	        }
	      }
	    };
	    httpRequest.send(null);
	  }
	};

	tourapp.xml.InsertTransformTo.prototype.transformAndInsert = function () {
	  if (typeof XSLTProcessor != 'undefined') {
	    var xsltProcessor = new XSLTProcessor();
	    xsltProcessor.importStylesheet(this.xsl);
	    var frag = xsltProcessor.transformToFragment(this.xml, this.elementToInsert.ownerDocument);
	    this.elementToInsert.appendChild(frag);
	  }
	  else if (typeof this.xml.transformNode != 'undefined') {
	    this.elementToInsert.insertAdjacentHTML('beforeEnd', this.xml.transformNode(this.xsl));
	  }
	  
	  if (this.handler)
		this.handler(this.elementToInsert);
	};

}
