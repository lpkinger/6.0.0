/**
 * 订阅项批量检测
 */	
Ext.define('erp.view.core.button.SubsBatchTest',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubsBatchTestButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'subsbatchtestbutton',
    	text : $I18N.common.button.erpSubsBatchTestButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function() {
			var grid = Ext.getCmp('batchDealGridPanel'), items = grid.selModel.getSelection(),
				idArray = [], ids = '',count = items.length;
			if(count == 0){
				Ext.Msg.alert("提示", "请勾选需要检测的项目!");
				return;
			}
	        Ext.each(items, function(item, index) {
	        	var id = item.get('ID_');
	        	if(id != undefined){
	        		idArray.push(id);
	        	}
	        });
	        ids = idArray.join(',');
	        Ext.getBody().mask("正在检测"); 
			Ext.Ajax.request({
				url: basePath + 'common/charts/batchTestSubsFormula.action',
				params: {
					ids: ids,
					caller:caller
				},
				sync: false,
				method: 'post',
				callback: function(options, success, response){
					Ext.getBody().unmask();
					var success = Ext.decode(response.responseText).success;
					if(success){
						var errorIdStr = Ext.decode(response.responseText).ids,
							errorIdArray = [],errorCount = 0;
						if(errorIdStr.length > 0){
							errorIdArray = errorIdStr.split(',');
							errorCount = errorIdArray.length;
						}
						var formPanel = Ext.getCmp('dealform');
						formPanel.onQuery();
						showMessage('批量检测完成：共检测'+count+'条，'+((errorCount>0)?('检测通过'+(count-errorCount)+'条;不通过<font style="color:red;">'+errorCount+'</font>条。'):'全部通过。'));
					}
				}
			});
		}
	});