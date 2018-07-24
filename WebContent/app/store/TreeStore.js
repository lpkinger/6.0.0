/**
 * 
 * 
 * 
 */
Ext.define('erp.store.TreeStore', {
    extend: 'Ext.data.TreeStore',
    alias:'widget.erpTreeStore', 
    root : {
    	text: 'root',
    	id: 'root',
		expanded: true
	}
});
/*Ext.define('erp.store.TreeStore', {
    extend: 'Ext.data.TreeStore',
    alias: 'widget.erpTreeStore',
    autoLoad: true,
    root : {
		expanded: true,
		children: []
	},
    proxy:{
		type: 'ajax',
		url: basePath + 'system/tree.action',
		reader: {
    		type: 'json',
    		root: 'tree'
    	}
	}
});*/