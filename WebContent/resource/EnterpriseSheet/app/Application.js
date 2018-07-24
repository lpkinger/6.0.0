/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('EnterpriseSheetApp.Application', {
    name: 'EnterpriseSheetApp',

    extend: 'Ext.app.Application',
    
    /* 
     * All the paths for custom classes
     */
    paths: {    	
        'EnterpriseSheet': 'src/EnterpriseSheet',           
        'Ext.ux': 'Ext.ux'
    },
    
    views: [
        // TODO: add views here
        'EnterpriseSheetApp.view.Viewport'
    ],

    controllers: [
        // TODO: add controllers here
    ],

    stores: [
        // TODO: add stores here
    ],
    
    launch: function() {    	
    	Ext.getDoc().on('keydown', function(e){    		
    		var target = e.getTarget();
    		if(e.BACKSPACE === e.getKey() && (target === document || target === document.body)){
    			e.preventDefault();
    		}
    	}, this);
        var docIdDom = document.getElementById('editFileId');
        var fileId = docIdDom ? docIdDom.getAttribute('data-value') : '';
        var url = window.location.href.toString();
        if(fileId && -1 === url.indexOf(fileId)){
           window.location.search = '?editFileId='+fileId;
           return;
        }
        if(SCOM.isEmptyValue(fileId) && window.location.search){
        	var search = window.location.search.split('=');
        	if(2 == search.length){
        		fileId = search[1];
        	}
        }
        Ext.create('EnterpriseSheetApp.view.Viewport', {
        	fileId: fileId
        });
           
    }
});
