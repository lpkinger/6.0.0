Ext.define('erp.view.core.button.ModifyDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpModifyDetailCommonButton',
		cls: 'x-btn-gray',
    	text: '更新明细数据',
    	//id:'modifyDetailbutton',
    	disabled:true,
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
			var me=this;
		},
		listeners: {
			afterrender:function(btn){
				var form=Ext.getCmp('form');
				var statuscodeField=form.statuscodeField;
				var status = Ext.getCmp(statuscodeField);
				if(status && status.value!= 'ENTERING'){
					btn.setDisabled(false);
				}
			},
			'enable':function(btn){
				var grid=btn.ownerCt.ownerCt;
				Ext.Array.forEach(grid.columns,function(c){
					if(c.modify){
						c.autoEdit=true;
					}
				
				});
			},
			click:function(btn){
				var grid=btn.ownerCt.ownerCt;
				var me=this;
				var s1='';
				//check所有grid是否已修改
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s1 = s1 + '<br/>' + msg;
					}
				}
				if(s1 == '' || s1 == '<br/>'){
					showError('还未修改数据.');
					return;
				}
				var param=new Array();
				var form=Ext.getCmp('form');
				var id=form.keyField;
				var log=caller+'|'+form.keyField+'='+Ext.getCmp(form.keyField).value;
				var params = new Object();
 				var param = grid.GridUtil.getGridStore(grid);
				params.param=unescape("[" + param.toString() + "]");
				params.caller=grid.caller==null? caller:grid.caller;
				params.log=log;
				grid.setLoading();
				Ext.Ajax.request({			
					url:basePath+'oa/modifyDetail.action',
					params:params,
					method:'post',
					callback:function(options,success,response){
						grid.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.success){
							showMessage('提示', '保存成功!', 1000);
							var u = String(window.location.href);
							window.location.reload();
						}else {
							var str = localJson.exceptionInfo;
							showError(str);return;
						}
					}
				});
				
			}
		}
	});