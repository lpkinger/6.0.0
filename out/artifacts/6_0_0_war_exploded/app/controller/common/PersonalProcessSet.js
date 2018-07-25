Ext.QuickTips.init();
Ext.define('erp.controller.common.PersonalProcessSet', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.JProcess.PersonalProcessSet','common.JProcess.ItemSelector'
     	],
    init:function(){
    	this.control({ 
    	});
    },
});