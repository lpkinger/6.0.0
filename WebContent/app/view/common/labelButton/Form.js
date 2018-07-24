Ext.define('erp.view.common.labelButton.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpLabelButtonFormPanel',
	id: 'labelbuttonform', 
    region: 'center',
    frame : true,
    autoScroll : true,
	defaultType : 'textfield',
	title:getUrlParam('text'),
	labelSeparator : ':',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	FormUtil: Ext.create('erp.util.FormUtil'),
	buttonAlign: 'center',
	buttons: [{
		name: 'confirm',
		text: $I18N.common.button.erpConfirmButton,
    	iconCls: 'x-button-icon-confirm',
    	cls: 'x-btn-gray',
    	style: {
    		marginLeft: '10px'
        }
	},{
		margin:'0 0 0 5',
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	confirmUrl:confirmUrl,
	initComponent : function(){
		var param = {caller: caller, condition: ''};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
		//this.initFields(this);
	}
});