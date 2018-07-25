/**
 * 手动执行自然切换的ECN
 */	
Ext.define('erp.view.core.button.ExecuteECNAuto',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExecuteECNAutoButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id:'ExecuteECNAutobutton',
    	text: "执行自然切换",
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}, 
		handler:function(btn) {
			Ext.Ajax.request({
				url: basePath + '/pm/bom/executeAutoECN.action',
				params: { },
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						Ext.Msg.alert("提示","已执行成功自然切换ECN");
						window.location.reload();
					}
				}
			});
		}
	});