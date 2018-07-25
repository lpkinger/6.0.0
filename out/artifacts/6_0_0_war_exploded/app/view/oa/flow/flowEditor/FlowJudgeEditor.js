Ext.define('erp.view.oa.flow.flowEditor.FlowJudgeEditor',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		//点击列表后跳转界面
		var value ;
		var valueButton;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fo_condition',
				caller : 'flow_operation',
				condition : 'fo_fdshortname = \''+ shortName +'\' and fo_name = \''+ name +'\''
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					var exp = new Ext.decode(rs.data);
					if(exp!=null && ''!=exp){
						exp = exp[0].FO_CONDITION;
						if(exp){
							value = Ext.String.trim(exp.substring(exp.indexOf('return')+6,exp.indexOf('};')));
							valueButton = Ext.String.trim(exp.substring(exp.indexOf('check(')+6,exp.indexOf('){')));
						}
					}
				}
			}
		});
		var buttonItems = new Array();
		var valueButtonArray = new Array();
		if(valueButton!=null && ''!=valueButton){
			if(valueButton.indexOf(',')>0){
				valueButtonArray = valueButton.split(',');
			}else{
				valueButtonArray.push(valueButton);
			}
		}
		
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field',
				caller : 'formdetail',
				condition : "fd_foid = (select fo_id from form where fo_caller = '"+caller + "') order by fd_detno"
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					var data = new Ext.decode(rs.data);
					Ext.Array.each(data,function(d){
						Ext.Array.each(valueButtonArray,function(b){
							if(d.FD_FIELD==b){
								var items = {
									xtype:'button',
									cls:'x-btn-gray',
									text:d.FD_CAPTION+':'+d.FD_FIELD,
									margin:'0 5 0 0',
									id:d.FD_FIELD,
									selectOnFocus: true, 
									listeners:{/*
										click:function(btn,e,o){
											e.preventDefault();
											var menu = new Ext.menu.Menu({
						                    	//控制右键菜单位置
						     				   	float:true,
						     				     items:[{
						     				     		text:"删除",
							       				      	iconCls:'leaf',
							       				      	handler:function(){
							       				      		var judgeButtonArea = Ext.getCmp('judgeButtonArea');
							       				      	    var items = judgeButtonArea.items.items;
							       				      	    console.log(items);
							       				      		Ext.Array.each(items,function(i,index){
							       				      			if(btn.id==i.id){
							       				      				judgeButtonArea.remove(index);
							       				      			}
							       				      		})
							       				      	}
						     				     }
						     				     ]
						                    }).showAt(e.getXY());//让右键菜单跟随鼠标位置
										}
									*/}
								};
								buttonItems.push(items);
							}
						});
					});
				}	
			}
		});
		Ext.apply(me, { 
			items: [{ 
				id:'judgeeditor',
				xtype:'panel',
				layout: 'fit', 
				cls:'judgeeditor',
				bbar: ['->',{
					xtype:'button',
					cls:'x-btn-gray',
					text:'保存',
					margin:'0 0 0 2',
					handler:function(){
						var judgeButtonArea = Ext.getCmp('judgeButtonArea');
						var field = new Array();
						Ext.Array.each(judgeButtonArea.items.items,function(i){
							field.push(i.id);
						});
						var judgeExpressionArea = Ext.getCmp('judgeExpressionArea');
						if(judgeExpressionArea.value){
							var value = Ext.util.Format.lowercase(judgeExpressionArea.value);
							var count = 0;
							var condition='function check('
						    Ext.Array.each(field,function(f,index){
						    	if(value.indexOf(Ext.util.Format.lowercase(f))<0){
						    		count++;
						    	}else {
						    		value.replace(value, f);
						    	}
						    	condition+=f+',';
						    });
						    if(count>0){
						    	showError('请确保表达式里面的字段有没有指定字段！');
					    		return false;
						    }
						    condition = condition.substring(0,condition.length-1);
						    condition +="){ return "+value+"};";
						    var remark = Ext.getCmp('remark').value;
						    Ext.Ajax.request({
						    	url:basePath+'oa/flow/saveJudgeOperation.action',
						    	params:{
						    		caller:caller,
						    		operation:name,
						    		nextNodeName:toNodeName,
						    		nodeName:fromNodeName,
						    		shortName:shortName,
						    		nextNodeId:toId,
						    		nodeId:fromId,
						    		condition:condition,
						    		remark:remark
						    	},
						    	callback : function(options,success,response){
									var rs = new Ext.decode(response.responseText);
									if(rs.exceptionInfo){
										showError(rs.exceptionInfo);return;
									}
									if(rs.success){
										Ext.Msg.alert('提示', '保存成功', function(){
											window.location.reload();
										});
									}
								}
						    });
						}else{
							showError('请填写表达式');
						}
					}
				},{xtype:'splitter',width:10},{
					xtype:'button',
					cls:'x-btn-gray',
					text:'关闭',
					margin:'0 5 0 0',
					handler:function(){
						parent.Ext.getCmp('operationEdit').close()
					}
				},'->'],
				items: [{
					xtype: 'panel',
					id:'judgePanel',
					layout: 'column', 
					items:[{
						fieldLabel: '操作名称',
						name: '操作名称',
						columnWidth:1,
						xtype:'textfield',
						labelAlign: 'left',
						readOnly:true,
						editable:false,
						value:name,
						cls: "form-field-allowBlank"
					},{
						fieldLabel: '描述',
						id: 'remark',
						readOnly:false,
						value:name,
						xtype: "textareafield",
					    maxLength: 300,
				        maxLengthText: "字段长度不能超过300字符!",
				        hideTrigger: false,
				        editable: true,
				        columnWidth: 1,
				        allowBlank: true,
				        cls: "form-field-allowBlank",
				        labelAlign: "left",
				        allowDecimals: true
					},{
						columnWidth: 1,
		            	title:'决策条件',
		            	xtype:'form',
		            	margin:'10 1 10 1',
		            	layout:'column',
		            	id:'flowJudgeEditor',
		            	defaults:{
		            		columnWidth:1,
		            		margin : '2 2 2 2'
		            	},items:[{ xtype: 'label',
		                    text: '指定字段:',
		                    margin:'10 0 10 0',
		                    columnWidth:0.072,
		                },{
		            		xtype:'panel',
		            		columnWidth:0.927,
		            		id:'judgeButtonArea',
		            		margin:'10 1 10 1',
		            		height:100,
		            		items:buttonItems
		            	},{
		            		xtype:'textarea',
		            		columnWidth:1,
		            		id:'judgeExpressionArea',
		            		margin:'10 1 10 1',
		            		height:100,
		            		name :'表达式区域',
		            		fieldLabel:'表达式区域',
		            		editable: true,
		            		readOnly:false,
		            		value:value
		            	}],
		            	fbar: ['->',{
							xtype:'button',
							cls:'x-btn-gray',
							text:'添加',
							id:'menuButton',
							margin:'0 0 0 2',
							menu:me.getItems(),
							handler:function(){
								
							}
						},{xtype:'splitter',width:100},{
							xtype:'button',
							cls:'x-btn-gray',
							text:'清除',
							margin:'0 5 0 0',
							handler:function(){
								var judgeButtonArea = Ext.getCmp('judgeButtonArea');
								var items = judgeButtonArea.items;   
								judgeButtonArea.removeAll();
								
								var judgeExpressionArea = Ext.getCmp('judgeExpressionArea');
								judgeExpressionArea.setValue('');
							}
						},'->']
					}]
				}]
			}]
		});
		
		me.callParent(arguments);
	},
	getItems:function(){
		var menu = Ext.create('Ext.menu.Menu', {
		    width: 150,
		    items: []
		});
		var s = '(select fo_id from form where fo_caller = \''+caller + '\') order by fd_detno ';
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field',
				caller : 'formdetail',
				condition : 'fd_foid = ' + s
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					menu.add({
						text:item.FD_CAPTION,
						field:item.FD_FIELD,
						handler:function(){
							var judgeButtonArea = Ext.getCmp('judgeButtonArea');
							if(!Ext.getCmp(item.FD_FIELD)){
								var items = [{
									xtype:'button',
									cls:'x-btn-gray',
									text:item.FD_CAPTION+':'+item.FD_FIELD,
									margin:'0 5 0 0',
									id:item.FD_FIELD,
									selectOnFocus: true, 
									listeners:{/*
										click:function(btn,e,o){
											e.preventDefault();
											var menu = new Ext.menu.Menu({
						                    	//控制右键菜单位置
						     				   	float:true,
						     				     items:[{
						     				     		text:"删除",
							       				      	iconCls:'leaf',
							       				      	handler:function(){
							       				      		var judgeButtonArea = Ext.getCmp('judgeButtonArea');
							       				      	    var items = judgeButtonArea.items.items;
							       				      	    console.log(items);
							       				      		Ext.Array.each(items,function(i,index){
							       				      			if(btn.id==i.id){
							       				      				judgeButtonArea.remove(index);
							       				      			}
							       				      		})
							       				      	}
						     				     	}
						     				     ]
						                    }).showAt(e.getXY());//让右键菜单跟随鼠标位置
										}
									*/}
								}];
								judgeButtonArea.add(items);
							}
						}
					});
				});
			}
		});
		return menu;
	}
});