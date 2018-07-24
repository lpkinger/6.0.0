Ext.define('erp.view.oa.publicAdmin.book.bookManage.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBookFormPanel',
	id: 'form', //
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			var grid = Ext.getCmp('grid');
			var form = Ext.getCmp('form');
			var condition = '';
			Ext.each(form.items.items, function(f){
				if(f.logic != null && f.logic != '' && f.value != null && f.value != ''){
					if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
						if(condition == ''){
							condition += f.logic + " " + f.value;
						} else {
							condition += ' AND ' + f.logic + " " + f.value;
						}
					} else {
						if(condition == ''){
							condition += f.logic + " = '" + f.value + "'";
						} else {
							condition += ' AND ' + f.logic + " = '" + f.value + "'";
						}
					}
				}
			});
			if(condition != ''){
				grid.getCount(caller, condition);
			} else {
				showError('请填写筛选条件');return;
			}
    	}
	}, '-', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		var param = {caller: caller, condition: ''};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
	}
});