Ext.define('erp.view.pm.make.Stepio',{ 
	extend: 'Ext.Viewport', 
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'pm/make/saveStepio.action' ,
					deleteUrl: 'pm/make/deleteStepio.action',
					updateUrl: 'pm/make/updateStepio.action',
					submitUrl: 'pm/make/submitStepio.action',
					auditUrl: 'pm/make/auditStepio.action',
					resAuditUrl: 'pm/make/resAuditStepio.action',			
					resSubmitUrl: 'pm/make/resSubmitStepio.action',
					checkUrl: 'pm/make/checkStepio.action',
					getIdUrl: 'common/getId.action?seq=stepio_SEQ',
					postUrl: 'pm/make/postStepIO.action',
					resPostUrl: 'pm/make/resPostStepIO.action',
					keyField: 'si_id',
					statusField: 'si_status',
					codeField: 'si_code',
					isStatic: caller=='Stepio!CraftBack' ?false: true 
				},{
					xtype:'erpDisplayGridPanel',
					anchor: caller=='Stepio!CraftScrap' ? '100% 30%':'100% 65%',
					title:'冲减明细',
					caller:'MakeClash',
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
						fieldLabel: '可冲减套料数'	
					},'-',{
						xtype:'numberfield',
						name:'clashqty',
						hideTrigger:true,
						id:'clashqty',
						fieldLabel: '本次冲减套数'	
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
								Ext.Ajax.request({
									url : basePath + 'common/checkFieldData.action',
									params: {
										caller: 'stepio',
										condition: "st_class in ('工序跳转','工序退制') and si_qty-nvl(st_clashqty,0)>0 and si_status='已过账' and si_makecode='" + Ext.getCmp('si_makecode').value + "' and st_inno= " + Ext.getCmp('st_outno').value
									},
									method : 'post',
									callback : function(options,success,response){
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);
											return false;
										}
										if(localJson.success){
											if(localJson.data){
												Ext.getCmp('saveclash').saveclash(null,[],null);
											}else{
												Ext.getCmp('saveclash').turn('StepIO!MakeClash!Deal', "st_class in ('工序跳转','工序退制') and si_qty-nvl(st_clashqty,0)>0 and si_status='已过账' and si_makecode='" + Ext.getCmp('si_makecode').value + "' and st_inno= " + Ext.getCmp('st_outno').value, '');
											} 
										}
									}
								});
							}else{
								var remain=Ext.getCmp('mcremain').value;
								var clash=Ext.getCmp('clashqty').value;
								if(remain<clash){
									showError('本次冲减套数不能大于可冲减套料数，请修改后再继续操作！');										
								}else{
									showError('本次冲减套数输入有误，请修改后再继续操作！');

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
				   				    			var data = grid.getEffectData();
				   				    			Ext.getCmp('saveclash').saveclash(btn,data,grid);
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
				        },
				        saveclash:function(btn,data,grid){
				        	if(btn) btn.setDisabled(true);
   				 			if(grid) grid.setLoading(true);
   				 			Ext.Ajax.request({
				 				url : basePath + 'pm/make/saveclash.action',
				 				params: {
				 					caller: caller,
				 					data: Ext.encode(data),
				 					id:Ext.getCmp('si_id').value,
				 					clashqty:Ext.getCmp('clashqty').value
				 				},
				 				method : 'post',
				 				async: false,
				 				callback : function(options,success,response){					   				 					
				 					if(grid) grid.setLoading(false);
				 					if(btn) btn.setDisabled(false);
				 				}
				 			});
				    			window.location.reload();}
				    	}],
					keyField:'mc_id',
					hidden:  caller=='Stepio!CraftBack' ? true :false,
					querycondition : 'mc_clashqty>0 and (mc_code,mc_class) IN (select si_code,st_class from stepio where @formCondition )',
					
				},{
					xtype: 'erpGridPanel2',
					id:'grid',
					//anchor: '100% 30%',
					detno: 'sd_detno',
					keyField: 'sd_id',
					title:'报废原因',
					caller:caller,
					mainField: 'sd_siid',
					hidden: caller=='Stepio!CraftScrap' ?false: true 
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});