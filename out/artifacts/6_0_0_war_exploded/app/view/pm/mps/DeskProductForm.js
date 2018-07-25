Ext.define('erp.view.pm.mps.DeskProductForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpDeskProductFormPanel',
	id: 'queryform', 
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
			var tab = Ext.getCmp('tabpanel');
		  var grid=tab.getActiveTab().items.items[0]; 
		   var form =Ext.getCmp('queryform');
		   BaseQueryCondition='';
			Ext.each(form.items.items, function(f){
				if(f.logic != null && f.logic != ''){
					if(f.xtype == 'checkbox' && f.value == true){
						if(BaseQueryCondition == ''){
							BaseQueryCondition += f.logic;
						} else {
							BaseQueryCondition += ' AND ' + f.logic;
						}
					} else if(f.xtype == 'datefield' && f.value != null){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
						if(BaseQueryCondition == ''){
							BaseQueryCondition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						} else {
							BaseQueryCondition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						}
					} else if(f.xtype == 'datetimefield' && f.value != null){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
						if(BaseQueryCondition == ''){
							BaseQueryCondition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						} else {
							BaseQueryCondition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						}
					} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
						if(BaseQueryCondition == ''){
							BaseQueryCondition += f.logic + '=' + f.value;
						} else {
							BaseQueryCondition += ' AND ' + f.logic + '=' + f.value;
						}
					} else {
						if(f.value != null && f.value != ''){
							if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
								if(BaseQueryCondition == ''){
									BaseQueryCondition += f.logic + " " + f.value;
								} else {
									BaseQueryCondition += ' AND (' + f.logic + " " + f.value + ")";
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
								if(BaseQueryCondition == ''){
									BaseQueryCondition += str;
								} else {
									BaseQueryCondition += ' AND (' + str + ")";
								}
							} else {
								if(BaseQueryCondition == ''){
									BaseQueryCondition += f.logic + "='" + f.value + "'";
								} else {
									BaseQueryCondition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			});
			//BaseQueryCondition=BaseQueryCondition==""?"pr_code=''":BaseQueryCondition;		
			grid.getCount(caller,BaseQueryCondition,grid.id);
    	}
	},{
		margin:'0 0 0 5',
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(b){
    		var form = b.ownerCt.ownerCt,
    			tab = form.ownerCt.down('tabpanel'),
    			ac = tab.getActiveTab(),
    			grid = ac.down('grid');
    		if(grid) {
    			if(grid.exportAction) {
    				grid.BaseUtil.customExport(caller, grid, ac.title, grid.exportAction, 
    						grid.gridcondition);
    			} else {
    				grid.BaseUtil.createExcel(caller, 'datalist', grid.gridcondition);
    			}
    		}
    	}
	},'->',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		var param = {caller: caller, condition: '',_noc:1};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
	}
});