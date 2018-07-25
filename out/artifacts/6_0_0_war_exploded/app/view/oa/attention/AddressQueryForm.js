Ext.define('erp.view.oa.attention.AddressQueryForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpAddressQueryFormPanel', 
    frame : true,
	layout : 'column',
	header: false,
	defaultType : 'textfield',
	caller:null,
	labelSeparator : ':',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	buttonAlign: 'left',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	buttons: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			var grid = Ext.getCmp('querygrid');
			var form = Ext.getCmp('queryform');
			var condition = grid.defaultCondition || '';
			Ext.each(form.items.items, function(f){
				if(f.logic != null && f.logic != ''){
					if(f.xtype == 'checkbox' && f.value == true){
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += ' AND ' + f.logic;
						}
					} else if(f.xtype == 'datefield' && f.value != null){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
						if(condition == ''){
							condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						} else {
							condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						}
					} else if(f.xtype == 'datetimefield' && f.value != null){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
						if(condition == ''){
							condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						} else {
							condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						}
					} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
						if(condition == ''){
							condition += f.logic + '=' + f.value;
						} else {
							condition += ' AND ' + f.logic + '=' + f.value;
						}
					} else {
						if(f.value != null && f.value != ''){
							if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value, '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += str;
								} else {
									condition += ' AND (' + str + ")";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			});
			var gridParam = {caller: caller, condition: condition};
			grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
    	}
	}, '-', {
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('querygrid');
    		grid.BaseUtil.exportexcel(grid);
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
	listeners:{
	  show:function(form){
	   var param = {caller: form.caller, condition: ''};
	   if(form.items.length<1){
    	form.FormUtil.getItemsAndButtons(form, 'common/singleFormItems.action', param);
    	}
	  }
	},
	initComponent : function(){ 
		this.callParent(arguments);
	}
});