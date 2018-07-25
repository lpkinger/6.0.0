Ext.define('erp.view.sysmng.MsgModelSetPanel',{
	extend:'Ext.form.Panel',
	alias:'widget.erpMsgModelSetPanel',
	id:caller + 'msgMdlSet',
	bodyCls:'formbase',
	layout:'column',
	actionData:null,
	disabled:false,
	border:false,
	autoDestroy:true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	disabledCls:'msg-form-disable',
	defaults:{
		disabledCls:'msg-form-disable'
	},
	style:'margin:10px',
	getActionData:function(){
		var actionData;
		Ext.Ajax.request({
			url:basePath + 'custommessage/getTree.action',
			method:'post',
			async:false,
			params:{
				caller:caller
			},
			callback:function(options,success,response){
				var res = Ext.decode(response.responseText);
				if(res.success){
					actionData = res.setting;
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
		return actionData;
	},
	listeners:{
		afterrender:function(self){
			var me = this;
			
			if(emtype!=='admin'){
				me.setDisabled(true);
			}
			
			if(caller){
				var actionData = me.getActionData();
				if(actionData.length>0){
					me.actionData = actionData;
					var moduleTitle = actionData[0].mm_name;
					me.add({
						xtype:'container',
						columnWidth:1,
						layout:'border',
						height:40,
						cls:'title',
						style:{
							margin:'0 0 10px 10px',
							padding:'5px',
							background:'#E6E4E6'
						},
						items:[{
							xtype:'tbtext',
							text:'【'+moduleTitle+'】知会消息模板设置',
							region:'center',
							style:{
								margin:'7px 0px 0px 0px',
								fontSize:'14px',
								fontWeight:'bold'
							}
						},{
							xtype:'button',
							cls:'savebtn',
							id:caller + 'saveBtn',
							text:'保存',
							width:85,
							region:'east',
							listeners:{
								afterrender:function(btn){
									if(emtype!='admin'){
										btn.hide();
									}
								},
								click:function(btn){
									var form = btn.ownerCt.ownerCt;
									if(me.actionData){
										var formArr = new Array();
										var gridArr = new Array();
										Ext.Array.each(me.actionData,function(item,index){
											var arr = new Array();
											var formData = new Object();
											var gridData = new Object();
											var prefix = item.mm_id+'_'+item.mm_operate;
											var actionCheckbox = Ext.getDom(prefix + 'Checkbox');
											formData.mm_isused = (actionCheckbox.checked?-1:0);
											var someValues = form.getForm().getValues();
											formData.mm_id = someValues[prefix + '_mm_id'];
											if(item.mm_isused!=formData.mm_isused){
												formArr.push(formData);
											}										
								
											gridData.mr_id = someValues[prefix + '_mr_id'];
											//gridData.mr_ispopwin = someValues[prefix + '_mr_ispopwin']=='1'?-1:0;
											gridData.mr_mans = someValues[prefix + '_mr_mans'];
											gridData.mr_manids = someValues[prefix + '_mr_manids'];
											gridData.mr_level = someValues[prefix + '_mr_level']?someValues[prefix + '_mr_level']:"";
											gridData.mr_isused = someValues[prefix + '_mr_isused'];
											if(!gridData.mr_manids){
												gridData.mr_isused = 0;
											}else{
												gridData.mr_isused = -1;
											}
											
											var fixedRole = new Object();
											Ext.Array.each(item.roles,function(role,index){
												if(role.mr_desc=='固定接收人'){
													fixedRole = role;
												}
											});	
											var keys = Object.keys(gridData);
											for(var i=0;i<keys.length;i++){
												var key = keys[i];
												if(gridData[key]!=fixedRole[key]){
													gridArr.push(gridData);
													break;
												}
											}
											
											var grid = Ext.getCmp(prefix + 'roleGrid');
											Ext.Array.each(grid.store.data.items,function(item,index){											
												if(item.dirty){
													var obj = new Object();
													obj.mr_id = item.data.mr_id;
													obj.mr_isused = item.data.mr_isused;
													obj.mr_ispopwin = item.data.mr_ispopwin?-1:0;
													obj.mr_level = item.data.mr_level;
													gridArr.push(obj);
												}
												
											});
										});
										if(formArr.length>0||gridArr.length>0){
											var params = new Object();
											Ext.Ajax.request({
												url:basePath + 'custommessage/save.action',
												method:'post',
												params:{
													formStore:Ext.encode(formArr),
													gridStore:Ext.encode(gridArr)
												},
												callback:function(options,success,response){
													var res = Ext.decode(response.responseText);
													if(res.success){
														showMessage('提示','保存成功!',1000);
														var grids = me.query('form');
														
														var actionData = me.getActionData();
														if(actionData){
															me.actionData = actionData;
														}
													}else if(res.exceptionInfo){
														showError(res.exceptionInfo);
													}
												}
											});
										}else{
											showMessage('未修改数据!');
										}										
									}
								},
								afterrender:function(btn){								
									if(emtype!='admin'){
										btn.hide();
									}
								}
							}
						}]
					});
					
					me.add({
						xtype:'tbtext',
						text:'【说明】此界面是用来设置'+moduleTitle+'执行某个操作后，发送知会消息到指定人或角色',
						columnWidth:1,
						style:{
							fontSize:'14px',
							padding:'0px 0px 0px 15px'
						}
					});
					
					Ext.Array.each(actionData,function(item,index){
						
						var prefix = item.mm_id+'_'+item.mm_operate;						
						var actId = prefix + 'Act';						
						
						me.add({
							xtype:'hidden',
							id:prefix + '_mm_id',
							name:prefix + '_mm_id',
							dataIndex:'mm_id'
						});
						
						me.add({
							xtype:'hidden',
							id:prefix + '_mr_id',
							name:prefix + '_mr_id',
							dataIndex:'mr_id'
						});
						
						me.add({
							xtype:'tbtext',
							id:actId,
							name:actId,
							columnWidth:1,
							style:{
								fontSize:'14px',
								fontWeight:'bold',
								color:'blue',
								padding:'30px 0px 0px 19px'
							}
						});
						
						var actCheckboxId = prefix + 'Checkbox';
						var checked = item.mm_isused==-1?'checked':'';
						var tplAct = new Ext.XTemplate(
					       '<tpl>',
					       '<span>执行操作：'+item.mm_operatedesc+'</span>',
					       '<input name="'+name+'" id="'+actCheckboxId+'" style="margin:0px 0px -3px 10px" class="mui-switch" onchange="changeActEnable(\''+actCheckboxId+'\',\''+prefix+'\')"  type="checkbox" '+checked+'><hr>',
					       '</tpl>'
						);
						tplAct.overwrite(Ext.get(actId), {});							
						me.add({
							xtype:'panel',
							collapsible:true,
							collapsed:item.mm_isused==0&&emtype=='admin',
							layout:'column',
							columnWidth:1,
							border:false,
							bodyStyle:'border-style:none;border:none',
							id:prefix+'content',
							items:[{
								xtype:'grid',
								title:'<span style="color:black!important;">常用知会角色:',
								id:prefix+'roleGrid',
								columnWidth:1,
								readOnly:item.mm_isused==-1?false:true,  //如果不启用，则只读
								columnLines:true,
								style:{
									margin:'16px 0 16px 16px'
								},
								plugins: [
							        Ext.create('Ext.grid.plugin.CellEditing', {
							            clicksToEdit: 1
							        })
							    ],
							    store:Ext.create('Ext.data.Store',{
							    	fields:['mr_isused','mr_desc','mr_ispopwin','mr_level','mr_messagedemo']
							    }),
								columns:{
									defaults:{
										align:'center'
									},
									items:[{
										header:'ID',
										hidden:true,
										dataIndex:'mr_id'
									},{
										header:'启用状态',	
										dataIndex:'mr_isused',	
									    renderer: function(value, cellmeta, record,x, y, store, view) {
										    var keyfield=this.keyField;
										    var keyvalue=record.data[this.keyField];
										    var flag=this.ownerCt.flag;
										    var dataIndex = this.columns[y].dataIndex;
									  	    cellmeta.style="padding:5px 0 0 0!important";
									  	    				  	    
										    if(value==-1){
										  	    return '<input class="mui-switch" onchange="changeEnable('+x+',\''+dataIndex+'\','+value+',\''+prefix+'\')"  type="checkbox" ' +
										  			'name="'+flag+'" data-id="'+prefix+keyvalue+'" checked>';
										    }else{
										  	    return '<input class="mui-switch" onchange="changeEnable('+x+',\''+dataIndex+'\','+value+',\''+prefix+'\')"  type="checkbox" ' +
										  			'name="'+flag+'" data-id="'+prefix+keyvalue+'">';
										    }
									    }
									},{
										header:'角色描述',
										dataIndex:'mr_desc',
										width:150,
										align:'left',
										style:'text-align:center'
									}/*,{
										header:'强制弹窗',
										dataIndex:'mr_ispopwin',
										xtype:'checkcolumn',
										headerCheckable : false,
										singleChecked: false,
										editor:{
								    	   xtype:'checkbox',
								    	   value:0,
								    	   cls: 'x-grid-checkheader-editor'
								        },
										rendererFn:function(value,meta,record){
											meta.style = 'padding:7px 0 0 0';
									        var cssPrefix = Ext.baseCSSPrefix,
									            cls = [cssPrefix + 'grid-checkheader'];
									
									        if (value) {   
									            cls.push(cssPrefix + 'grid-checkheader-checked');
									        }
									        return '<div class="' + cls.join(' ') + '">&#160;</div>';														
										},
									    processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
									   		var grid = this.ownerCt.ownerCt;
									   		if(grid.readOnly){
									   			return false;
									   		}
									        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
									        	var record = null;
									        	var dataIndex = this.dataIndex;
									        	var checked = null;
								        		record = view.panel.store.getAt(recordIndex);
								        		checked = !record.get(dataIndex);
									            record.set(dataIndex, checked);
									            this.fireEvent('checkchange', this, recordIndex, checked);
									            return false;
									        }
									    }
									}*/,{			
										header:'信息等级',
										dataIndex:'mr_level',					
										editor:{
											xtype:'combo',
											displayField:'display',
											valueField:'value',
											editable:false,
											store: Ext.create('Ext.data.Store', {
												fields: ['display', 'value'],
												data:[{
													display:'低',
													value:'低'
												},{
													display:'中',
													value:'中'
												},{
													display:'高',
													value:'高'
												}]
											})						
										}
									},{
										header:'信息详情样例',
										flex:1,
										dataIndex:'mr_messagedemo',
										align:'left',
										style:'text-align:center'
									}]	
								},
								listeners:{
									afterrender:function(grid){
										if(item.roles.length>0){
											var ir = null;									
											var arrConcat = item.roles.concat();
											Ext.Array.each(arrConcat,function(it,index){
												if(it.mr_desc=='固定接收人'){
													ir = it;
												}
											});
											if(ir){
												Ext.Array.remove(arrConcat,ir);
											}
											grid.store.loadData(arrConcat);										
										}
									}
								}
							},{
								xtype:'tbtext',
								text:'固定接收人:',
								columnWidth:1,
								style:{
									fontSize:'14px',
									padding:'0px 0px 0px 22px'
								}
							},{
								fieldLabel:'固定接收人',
								xtype:'HrOrgSelectfield',
								name:prefix+'_mr_mans',
								height:63,
								columnWidth:0.84,
								id:prefix+'_mr_mans',
								logic:prefix+'_mr_manids',
								style:'border:none',
								readOnly:false,
								secondname:prefix+'_mr_manids',						
								allowBlank:false,
								style:{
									margin:'11px 0 0 17px'
								},
								listeners:{
									afterrender:function(htmleditor){
										Ext.defer(function() {
											var editor = htmleditor.items.items[0];
											var iframe = editor.iframeEl.dom;
											var doc = (iframe.contentDocument)
													? iframe.contentDocument
													: iframe.contentWindow.document;
											if (doc && doc.body && doc.body.style) {
												doc.body.style['padding'] = '0px';
												doc.body.style['border-bottom'] = 'solid 1px #b5b8c8';
											}											
										}, 200);						
									}
								}
							},{
								xtype:'hidden',
								id:prefix+'_mr_manids',
								name:prefix+'_mr_manids'				
							},{
								xtype:'hidden',
								id:prefix + '_mr_isused',
								name:prefix + '_mr_isused'
							},/*{
								xtype:'checkboxfield',
								name:prefix+'_mr_ispopwin',
								id:prefix+'_mr_ispopwin',
								inputValue: '1',
								boxLabel:'强制弹窗',
								columnWidth:0.14,
								style:{
									margin:'17px 0 17px 0'
								}
							},*/{
								xtype:'combo',
								fieldLabel: "信息等级",  
								labelWidth:70,
								id:prefix+'_mr_level',
								name:prefix+'_mr_level',
								columnWidth:0.16,
								displayField:'display',
								valueField:'value',
								editable: false, 
								store: Ext.create('Ext.data.Store', {
									fields: ['display', 'value'],
									data:[{
										display:'低',
										value:'低'
									},{
										display:'中',
										value:'中'
									},{
										display:'高',
										value:'高'
									}]
								}),
								style:{
									margin:'18px 0 17px 0'
								}
								
							}],
							listeners:{
								afterrender:function(form){
									var head = form.getHeader();
									head.removeCls(['x-panel-header-default','x-panel-header-default-top']);
								}
							}
						});
						
						var form = Ext.getCmp(caller + 'msgMdlSet');
						if(item.roles.length>0){
							Ext.Array.each(item.roles,function(i,index){
								if(i.mr_desc=='固定接收人'){
									var obj = new Object();
									obj[prefix + '_mr_id'] = i.mr_id;
									obj[prefix + '_mr_mans'] = i.mr_mans;
									obj[prefix + '_mr_manids'] = i.mr_manids;
									//obj[prefix + '_mr_ispopwin'] = i.mr_ispopwin==-1?1:0;
									obj[prefix + '_mr_level'] = i.mr_level;
									obj[prefix + '_mr_isused'] = i.mr_isused;

									form.getForm().setValues(obj);
									
									var mrmans = Ext.getCmp(prefix + '_mr_mans');
									var mrmanids = Ext.getCmp(prefix + '_mr_manids');
									mrmans.value = i.mr_mans;
									mrmanids.value = i.mr_manids;
								}
							});
						}
						
						var obj = new Object();
						obj[prefix + '_mm_id'] = item.mm_id;
	
						form.getForm().setValues(obj);
					});
				}else{
					
					me.add({							
							columnWidth:1,
							id:'myform',
							height:400,
							border:false,
							width:'100%',
							xtype: 'form',							
							bodyStyle: 'background:#fffff;',
							html:'<div style="left:40%;position:absolute;top:40%;font-weight:bold;font-size:25px;color:rgba(144, 143, 143, 0.5)">该单据还未设置知会模板</div>'													
					});
					
				}
				
			}
			
			me.doLayout();
		}
	}
});