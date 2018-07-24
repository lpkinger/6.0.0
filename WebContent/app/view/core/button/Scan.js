/**
 * 查找按钮
 */	
Ext.define('erp.view.core.button.Scan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScanButton',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpScanButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			var form = Ext.getCmp('form');
			var url = 'jsps/common/datalist.jsp?_noc=1&whoami=' + caller;
			if(btn.urlcondition){
				url += '&urlcondition=' + btn.urlcondition;
			}
			form.FormUtil.onAdd(caller + '_scan', parent.Ext.getCmp('content-panel').getActiveTab().title + 'DataList', url);
		}
	});