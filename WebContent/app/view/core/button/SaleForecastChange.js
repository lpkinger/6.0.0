Ext.define('erp.view.core.button.SaleForecastChange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaleForecastChangeButton',
		text:$I18N.common.button.erpSaleForecastChangeButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
        handler: function(btn){
        	var status=Ext.getCmp('sf_statuscode').getValue();
        	if(status =='AUDITED'){
        		var main = parent.Ext.getCmp("content-panel");
        		var currenttab = main.getActiveTab();
        		var detaildatas = Ext.getCmp('grid').store.data.items;
        		currenttab.detaildatas = detaildatas;
        		Ext.create('erp.util.FormUtil').onAdd('addSaleForecastChange', '销售预测变更单', 'jsps/pm/mps/saleForecastChange.jsp?code='+Ext.getCmp('sf_code').value + '&tabid=' + currenttab.id);
        	}else showMessage('提示','当前状态不允许新增变更单',1000);
        	
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});