/**
 * 直接插入excel导入数据
 */
Ext.define('erp.view.core.button.DirectImportUpExcel', {
	extend : 'Ext.form.Panel',
	alias : 'widget.directimportupexcel',
	initComponent : function() {
		if (this.iconCls) {
			this.items[0].buttonConfig.iconCls = this.iconCls;
		}
		if (this.cls) {
			this.items[0].buttonConfig.cls = this.cls;
		}
		if (this.itemCls) {
			this.items[0].buttonConfig.cls = this.itemCls;
		}
		if (this.iconCls) {
			this.items[0].buttonConfig.text = this.text;
		}
		this.callParent(arguments);
	},
	bodyStyle : 'background: transparent no-repeat 0 0;border: none;',
	items : [ {
		xtype : 'filefield',
		name : 'file',
		buttonOnly : true,
		hideLabel : true,
		width : 62,
		height : 17,
		buttonConfig : {
			iconCls : 'x-button-icon-excel',
			cls : 'x-btn-gray',
			text : $I18N.common.button.erpUpExcelButton
		},
		listeners : {
			change : function(field) {
				field.ownerCt.upexcel(field);
			}
		}
	} ],
	upexcel : function(field) {
		var me=this;
		var bool = this.fireEvent('beforeimport', this);
		warnMsg('直接导入会清除原有明细数据，确定直接导入吗', function(btn){
			if(btn == 'yes'){
				var form =Ext.getCmp('form'), keyValue=Ext.getCmp(form.keyField).value;
				if (bool != false) {
					me.getForm().submit({
						url : basePath + 'common/directInsertByExcel.action?caller=' + caller+'&keyValue='+keyValue,
						waitMsg : "正在解析Excel",
						timeout:100000,
						success : function(fp, o) {
							field.reset();
							Ext.Msg.show({ 
								title : '提示', 
								msg : '导入成功!', 
								buttons: Ext.Msg.OK, 
								fn: function(){
									var grid=me.ownerCt.floatParent.ownerCt.ownerCt;
				           		    var param={
				           		    	caller:caller,
				           		    	condition:grid.mainField+'='+keyValue
				           		    };
				           			grid.GridUtil.loadNewStore(grid,param);
								}, 
								closable: false 
							});
						},
						failure : function(fp, o) {
							if (o.result.size) {
								showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
								field.reset();
							} else {
								showError(o.result.error);
								field.reset();
							}
						}
					});
				}
			}else{
				field.reset();
			}
		});
	}
});