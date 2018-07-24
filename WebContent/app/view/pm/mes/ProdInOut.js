Ext.define('erp.view.pm.mes.ProdInOut',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/mes/saveProdInOut.action?caller=' +caller,
					deleteUrl: 'pm/mes/deleteProdInOut.action?caller=' +caller,
					updateUrl: 'pm/mes/updateProdInOut.action?caller=' +caller,
					auditUrl: 'pm/mes/auditProdInOut.action?caller=' +caller,
					resAuditUrl: 'pm/mes/resAuditProdInOut.action?caller=' +caller,
					submitUrl: 'pm/mes/submitProdInOut.action?caller=' +caller,
					resSubmitUrl: 'pm/mes/resSubmitProdInOut.action?caller=' +caller,
					postUrl: 'pm/mes/postProdInOut.action?caller=' +caller,
					resPostUrl: 'pm/mes/resPostProdInOut.action?caller=' +caller,
					printUrl: 'pm/mes/printProdInOut.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PRODINOUT_SEQ', 
					statusField: 'pi_invostatus',
					statuscodeField: 'pi_invostatuscode',
					statusCode: 'pi_statuscode'//过账状态
				},{
					xtype: 'erpDisplayGridPanel',
					anchor: '100% 60%', 
					tbar:[{
						xtype:'numberfield',
						name:'mconmake',
						id:'mconmake',
						readOnly:true,
						fieldLabel: '在制数量'
					},'-',{
						xtype:'numberfield',
						name:'mcremain',
						id:'mcremain',
						readOnly:true,
						fieldLabel: '可冲减原料数'	
					},'-',{
						xtype:'numberfield',
						name:'clashqty',
						hideTrigger:true,
						id:'clashqty',
						fieldLabel: '本次冲减原料数'	,
						readOnly: caller=="ProdInOut!ProcessReturn"
					},'->',{
						xtype:'button',
						id: 'setclash',
						hidden:true,
						text:'设置冲减',
						baseCls:'baseconfirmbutton' 
					},{
						xtype:'button',
						id: 'saveclash',
						hidden:true,
						text:'确定冲减',
						baseCls:'baseconfirmbutton' ,
						handler:function(){
							if(Ext.getCmp('clashqty').isValid()){
								Ext.getCmp('saveclash').turn('StepIO!MakeClash!Deal', "st_class in ('工序跳转','工序退制') and si_qty-nvl(st_clashqty,0)>0 and si_status='已过账' and si_makecode='" + Ext.getCmp('pd_ordercode').value + "' and st_inno= " + Ext.getCmp('pd_orderdetno').value, '');
							}else{
								var remain=Ext.getCmp('mcremain').value;
								var clash=Ext.getCmp('clashqty').value;
								if(remain<clash){
									showError('本次冲减数不能大于可冲减数，请修改后再继续操作！');										
								}else{
									showError('本次冲减数输入有误，请修改后再继续操作！');

								}								} 
						},
						turn: function(nCaller, condition, url){
					    	var win = new Ext.window.Window({
						    	id : 'win',
								    height: "100%",
								    width: "80%",
								    maximizable : true,
									buttonAlign : 'center',
									layout : 'anchor',
								    items: [{
								    	  tag : 'iframe',
								    	  frame : true,
								    	  anchor : '100% 100%',
								    	  layout : 'fit',
								    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
								    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
								    }],
								    buttons : [{
								    	name: 'confirm',
								    	text : $I18N.common.button.erpConfirmButton,
								    	iconCls: 'x-button-icon-confirm',
								    	cls: 'x-btn-gray',
								    	listeners: {
				   				    		buffer: 500,
				   				    		click: function(btn) {
				   				    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
				   				    			btn.setDisabled(true);
				   				    			var data = grid.getEffectData();
					   				 			grid.setLoading(true);
					   				 			Ext.Ajax.request({
				   				 				url : basePath + 'pm/mes/saveProdIOClash.action',
				   				 				params: {
				   				 					caller: caller,
				   				 					data: Ext.encode(data),
				   				 					id:Ext.getCmp('pd_id').value,
				   				 					clashqty:Ext.getCmp('clashqty').value
				   				 				},
				   				 				method : 'post',
				   				 				async: false,
				   				 				callback : function(options,success,response){					   				 					
				   				 					grid.setLoading(false);
				   				 					btn.setDisabled(false);
				   				 				}
				   				 			});
				   				    			window.location.reload();
				   				    		}
								    	}
								    }, {
								    	text : $I18N.common.button.erpCloseButton,
								    	iconCls: 'x-button-icon-close',
								    	cls: 'x-btn-gray',
								    	handler : function(){
								    		Ext.getCmp('win').close();
								    	}
								    }]
								});
								win.show();
				        }
					}],
					keyField:'mc_id',
					querycondition : 'mc_clashqty>0 and (mc_code,mc_class) IN (select pi_inoutno,pi_class from prodinout where @formCondition )',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});