Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOM', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.BOM','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.BomCopy',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.button.CallProcedureByConfig',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.YnColumn','core.button.Flow','core.button.Print',
    		'core.button.SonBOM','core.button.Replace','core.button.FeatureDefinition','core.button.PrintByCondition',
    		'core.button.Banned','core.button.ResBanned','core.form.FileField','core.button.Sync','core.button.FeatureQuery',
    		'core.button.LoadRelation','core.button.Modify',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField','core.button.BOMTurn','core.button.BomUpdatePast'
    		,'core.form.HrOrgSelectField','core.button.ModifyDetail'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'textfield[name=bo_ispast]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				Ext.getCmp('sonbom').setDisabled(false);
    				Ext.getCmp('replace').setDisabled(false); 
    				//Ext.getCmp('featuredefinition').setDisabled(false);
    				//Ext.getCmp('FeatureQuery').setDisabled(false);
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					iconCls:null,
    					xtype: 'erpSonBOMButton'
    				});
    				btn.ownerCt.add({
    					iconCls:null,
    					xtype: 'erpReplaceButton'
    				}); 
    				btn.ownerCt.add({
    					xtype: 'erpFeatureDefinitionButton'
    				});
    				btn.ownerCt.add({
    					xtype: 'erpFeatureQueryButton'
    				});
    			}
    		},
    		'erpBomTurnButton':{
    			afterrender:function(btn){
       			 var statuscode=Ext.getCmp('bo_statuscode').getValue();
       			 if(statuscode&&statuscode!='AUDITED'){
       				 btn.hide();
       			 }
    			}
    		}, 
    	   'erpBomCopyButton':{
    		/* afterrender:function(btn){
    			 var statuscode=Ext.getCmp('bo_statuscode').getValue();
    			 if(statuscode&&statuscode!='AUDITED'){
    				 btn.hide();
    			 }
    		 }, */
    		  click:function(btn){
    			  Ext.create('Ext.window.Window', {
    					title: '复制BOM',
    					height: 200,
    					width: 320,
    					layout: 'column',
    					id:'win',
    					buttonAlign:'center',
//    					defaults:{
//    						fieldStyle:'background:#FFFAFA;color:#515151;',
//    						columnWidth:1
//    					},
    					autoScroll:false,
    					allowDrag:false,
    					items: [{
    						xtype:'dbfindtrigger',
    					    fieldLabel:'母件编号',
    					    name:'mothercode',
    					    id:'mothercode',
    					    fieldStyle:'background:#fffac0;color:#515151;',
    					    allowBlank:false
    					},{
    					    xtype:'textfield',
    					    fieldLabel:'母件名称',
    					    name:'motherdetail',
    					    id:'motherdetail',
    					    allowBlank:true
    					 },{
    					    xtype:'textfield',
    					    fieldLabel:'规格',
    					    name:'motherspec',
    					    id:'motherspec',
    					    allowBlank:true
    					 },{
    						 xtype:'hidden',
    						 name:'motherid',
    						 id:'motherid'
    					 }],
    					 buttons:[{
    						 text:$I18N.common.button.erpConfirmButton,	 
    						 xtype:'button',
    						 formBind: true,
    						 handler:function(){
    							 var id=Ext.getCmp('motherid').getValue();
    							 var form=Ext.getCmp('form');
    							 var r=form.getValues();
    			    			  var keys = Ext.Object.getKeys(r), f;
    			    				var reg = /[!@#$%^&*()'":,\/?]/;
    			    				Ext.each(keys, function(k){
    			    					f = form.down('#' + k);
    			    					if(f && f.logic == 'ignore') {
    			    						delete r[k];
    			    					}
    			    					//codeField值强制大写,自动过滤特殊字符
    			    					if(k == form.codeField && !Ext.isEmpty(r[k])) {
    			    						r[k] = r[k].trim().toUpperCase().replace(reg, '');
    			    					}
    			    					if(contains(k, 'ext-', true)){
    			    						delete r[k];
    			    					}
    			    				}); 			    				
    			    				var	grid = Ext.getCmp('grid');
    			    				var	jsonGridData = new Array();
    			    				var form = Ext.getCmp('form');
    			    				var s = grid.getStore().data.items;//获取store里面的数据
    			    				var dd;
    			    				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
    			    					var data = s[i].data;
    			    					dd = new Object();
    			    						Ext.each(grid.columns, function(c){
    			    							if((!c.isCheckerHd)&&(c.logic != 'ignore')){//只需显示，无需后台操作的字段，自动略去
    			    								
    			    								if(c.xtype == 'datecolumn'){
    			    									c.format = c.format || 'Y-m-d';
    			    									if(Ext.isDate(data[c.dataIndex])){
    			    										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
    			    									} else {
    			    										if(c.editor){
    			    											dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
    			    										}
    			    									}
    			    								} else if(c.xtype == 'datetimecolumn'){
    			    									if(Ext.isDate(data[c.dataIndex])){
    			    										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
    			    									} else {
    			    										if(c.editor){
    			    											dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
    			    										}
    			    									}
    			    								} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
    			    									if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
    			    										dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
    			    									} else {
    			    										dd[c.dataIndex] = s[i].data[c.dataIndex];
    			    									}
    			    								} else {
    			    									dd[c.dataIndex] = s[i].data[c.dataIndex];
    			    								}
    			    							}
    			    						});
    			    						if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
    			    							dd[grid.mainField] = Ext.getCmp(form.keyField).value;
    			    						}
    			    						jsonGridData.push(Ext.JSON.encode(dd));
    			    				}
    			    				var params=new Object();
    			    				params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    			    				params.param = unescape(jsonGridData.toString().replace(/\\/g,"%"));
    			    				params.id=id;
    			    				if(id == null ||id == ''){
    			    					showError("请先选择母件编号");
    	    	    					return;
    			    				}
    			    				Ext.Ajax.request({
    			    		        	url : basePath +'pm/bom/bomcopy.action',
    			    		        	params:params,
    			    		        	method : 'post',
    			    		        	callback : function(options,success,response){
    			    		        		var res = new Ext.decode(response.responseText);
    			    		        		if(res.exceptionInfo != null){
    			    		        			showError(res.exceptionInfo);return;
    			    		        		}else {
    			    		        			Ext.Msg.alert('提示','赋值失败');
    			    		        		}
    			    		        	}
    			    		        }); 
    						 }
    					 },{
    						 text:$I18N.common.button.erpCancelButton,
    						 handler:function(){
    							 Ext.getCmp('win').close();
    						 }
    					 }]
    				}).show();
    		  }
    		 
    	    },
    		'erpSonBOMButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				Ext.Ajax.request({
    		        	url : basePath + "pm/bom/getDescription.action",
    		        	params: {
    		        		tablename: 'BOM',
    		        		field: 'bo_id',
    		    			condition: "bo_mothercode='" + record.data['bd_soncode'] + "'"
    		        	},
    		        	method : 'post',
    		        	async: false,
    		        	callback : function(options,success,response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.success){
    		        			sonid = res.description;
    		        		}
    		        	}
    				});   
    				if(sonid != null && id != '' && sonid != 0 && sonid != '0'){
    					me.FormUtil.onAdd(null, '下级BOM资料', 'jsps/pm/bom/BOM.jsp?formCondition=bo_id=' + sonid + 
        						"&gridCondition=bd_bomid=" + sonid + "&_noc=1");
    				}
    			}
    		},  
    		'erpReplaceButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				var id = record.data['bd_id'];
    				var bomid=record.data['bd_bomid'];
    				var main = parent.Ext.getCmp("content-panel");
    				var panelId=main.getActiveTab().id;
    			    main.getActiveTab().currentGrid=Ext.getCmp('grid');
    				if(id != null && id != '' && id != 0 && id != '0'){
    					me.FormUtil.onAdd('replaceBOM' + id, '替代关系维护', 'jsps/pm/bom/prodReplace.jsp?formCondition=bd_id=' + id + 
        						"&gridCondition=pre_bdid=" + id + "&_noc=1&panelId="+panelId+"&bomid="+bomid);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt; 
    				if(Ext.getCmp(form.codeField)){
    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('BOM',1,'bo_code');//自动添加编号
    				  }
    				}  				
    				this.FormUtil.beforeSave(this);   				
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('bo_id').value == null || Ext.getCmp('bo_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('bo_id').value == null || Ext.getCmp('bo_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="SBOMList2";
				var condition='{Bom.bo_id}='+Ext.getCmp('bo_id').value+''+' and (isnull({BomDetail.bd_usestatus}) or {BomDetail.bd_usestatus}<>'+"'DISABLE')";
				var id=Ext.getCmp('bo_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBOM', '新增BOM资料', 'jsps/pm/bom/BOM.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if((Ext.getCmp('bo_id').value == null || Ext.getCmp('bo_id').value == '') 
    						|| (status && status.value != 'ENTERING')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				/*Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['bd_baseqty'] == null || item.data['bd_baseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['bd_detno'] + "行未填写单位用量，不能提交！");return;
    						}
    					}
    				});*/
    				if(bool){
    					//wanlida bom明细有为空的情况。
    					me.FormUtil.onSubmit(Ext.getCmp('bo_id').value,true);
    				}			
    			}
    		},
    		'erpBannedButton':{
    			afterrender:function(btn){
          			 var statuscode=Ext.getCmp('bo_statuscode').getValue();
          			 if(statuscode&&statuscode=='DISABLE'){
          				 btn.hide();
          			 }
    				 if(statuscode && statuscode != 'AUDITED'){
    					 btn.hide();
    				 }
       			},
    			click:function(btn){
    				//bom 禁用
    				me.changeBOM("Banned","禁用");
    			}
    		},
    		'erpResBannedButton':{
    			afterrender:function(btn){
          			 var statuscode=Ext.getCmp('bo_statuscode').getValue();
          			 if(statuscode&&statuscode!='DISABLE'){
          				 btn.hide();
          			 }
       			},
    			click:function(btn){
    				//bom 反禁用
    				me.changeBOM("ResBanned",'反禁用');
    			}
    		},
    		'erpFeatureDefinitionButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.bd_soncode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.bd_soncode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
    									var formCondition="pr_code='"+record.data.bd_soncode+"'";
    									var gridCondition="pf_prodcode='"+record.data.bd_soncode+"'";
    									var win = new Ext.window.Window({
    			    						id : 'win',
    			    						title: '物料特征项设置',
    			    						height: "90%",
    			    						width: "70%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.bd_soncode + '" src="' + basePath + 
    			    							'jsps/pm/bom/ProdFeature.jsp?formCondition='+formCondition+'&&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();    									
    								} else {
    									showError('物料特征必须为虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpFeatureQueryButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.bd_soncode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.bd_soncode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);
    								return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
    									var win = new Ext.window.Window({
    			    						id : 'win',
    			    						title: '生成特征料号',
    			    						height: "90%",
    			    						width: "90%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    			    							"jsps/common/datalist.jsp?whoami=BomFeatureQuery&_noc=1&urlcondition=fp_refno='" + record.data.bd_soncode +"'"+'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show(); 
    								} else {
    									showError('物料特征必须为虚拟特征件');return;
    								}
    							}
    						}
    					});
					}
				}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpBomUpdatePastButton': { 
    			afterrender:function(btn){  
          			 if(!Ext.getCmp('bo_id').getValue()>0){
          				btn.hide();
          			 }
       			},
    			click: function(btn){
    				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "pm/bom/updateBOMPast.action",
						params: {
							bo_id:Ext.getCmp('bo_id').value,
							value:Ext.getCmp('bo_ispast').value
						},
						method : 'post',
						async: false,
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								return;
							}else{
								Ext.Msg.alert('提示','更新成功');
								return;
							}
						 
						}
					});
				}
    		},
    		'field[name=bo_id]': {
    			change: function(f){
    				if(f.value != null){
    					formCondition = 'bo_idIS' + f.value;
    					gridCondition = 'bd_boidIS' + f.value;
    				//	me.changeButtons(Ext.getCmp('bo_statuscode').value);
    				}
    			}
    		},
    		'field[name=bo_statuscode]': {
    			change: function(f){
    				if(f.value != null){
    					me.changeButtons(f.value);
    				}
    			}
    		},
    		'dbfindtrigger[name=bo_mothercode]': {
    			aftertrigger: function(f){
    				if(f.isDirty()){
    					 Ext.Ajax.request({
    					   	  url : basePath +'/pm/bom/getDescription.action',
    					   	  params :{
    					   		   caller:'BOM',
    					   		   tablename:'BOM',
    					   		   field:'bo_id',
    					   		   condition:"bo_mothercode='"+f.value+"'"
    					   	  },
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
//    					   			if(localJson.description != null){
//    			    			        window.location.href = basePath + "jsps/pm/bom/BOM.jsp?formCondition=bo_idIS'" +localJson.description+"'&gridCondition=bd_bomidIS'"+localJson.description+"'";
//    					   			}
    				   			} else if(localJson.exceptionInfo){
    				   				showError(res.exceptionInfo);return;			   			
    					   	   } else{
    				   				return;
    				   		 	}
    					   	  }
    					   });  
    				}		
    			}
    		},
    		'dbfindtrigger[name=bd_soncode]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var grid = Ext.getCmp('grid');
    				var p = Ext.getCmp('bo_mothercode').value;
    				if(p != null && p!= ''){
    					var c = "(pr_code<>'" + p + "'";	        				
	        			t.dbBaseCondition = c + ")";
    				} else {
    					showError('未选择父件');return;
    				}
    			},
    		/*	change: function(t){
    				
    			},*/
    			aftertrigger: function(t){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				me.FormUtil.getFieldsValue('BOM', 'bo_id', "bo_mothercode='" + t.value + "'", 'bd_sonbomid', grid.selModel.lastSelected);
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#bo_statuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            },
            'erpModifyCommonButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('bo_statuscode');
					if(status && status.value == 'AUDITED'){
						btn.setText('更新BOM备注');
						btn.setWidth(120);
						btn.show();//触发字段可编辑
					}
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	changeButtons: function(value){
		switch(value){
			case 'ENTERING':
				if(Ext.getCmp('bo_id').value != null){
					Ext.getCmp('save').hide();
					Ext.getCmp('updatebutton').show();
					Ext.getCmp('deletebutton').show();
					Ext.getCmp('submit').show();
				} else {
					Ext.getCmp('save').show();
					Ext.getCmp('updatebutton').hide();
					Ext.getCmp('submit').hide();
					Ext.getCmp('deletebutton').hide();
				}
				Ext.getCmp('resSubmit').hide();
				Ext.getCmp('auditbutton').hide();
				Ext.getCmp('resAudit').hide();
				break;
			case 'COMMITED':
				Ext.getCmp('save').hide();
				Ext.getCmp('updatebutton').hide();
				Ext.getCmp('submit').hide();
				Ext.getCmp('resSubmit').show();
				Ext.getCmp('auditbutton').show();
				Ext.getCmp('resAudit').hide();
				Ext.getCmp('deletebutton').hide();
				break;
			case 'AUDITED':
				Ext.getCmp('save').hide();
				Ext.getCmp('updatebutton').hide();
				Ext.getCmp('submit').hide();
				Ext.getCmp('resSubmit').hide();
				Ext.getCmp('auditbutton').hide();
				Ext.getCmp('resAudit').show();
				Ext.getCmp('deletebutton').hide();
				break;
		}
	},
	changeBOM:function(type,title){
		Ext.create('Ext.window.Window',{
       	 width:350,
       	 height:185,
       	 id:'win',
       	 title:'<h1>'+title+'BOM</h1>',
       	 layout:'column',
       	 items:[{
       		 xtype:'textarea',
       		 columnWidth:1,
       		 fieldLabel:title+'原因',
       	     name:'remark',
       	     editable:true,
       	     id:'remark',
       	     fieldStyle:'background:#fffac0;color:#515151;'
       	 }],
       	 buttonAlign:'center',
       	 buttons:[{
 				xtype:'button',
 				columnWidth:0.12,
 				text:'保存',
 				width:60,
 				iconCls: 'x-button-icon-save',
 				handler:function(btn){
 					var remark=Ext.getCmp('remark').getValue();	
 					var boid=Ext.getCmp('bo_id').getValue(); 
 				    if(!remark){
 					showError('请先填写相应的'+title+'原因!') ;  
 					return;
 				    }else{
 				    	var dd=new Object(); 
 				    	dd['boid']=boid;
 				    	dd['remark']=remark;
 				    	var dealurl ='';
 				    	if (type=='Banned'){
 				    		dealurl='pm/bom/bannedBOM.action';
 				    	}else if (type=='ResBanned'){
 				    		dealurl='pm/bom/resBannedBOM.action';
 				    	}else{
 				    		showError('错误调用') ; 
 				    		return;
 				    	}
 					   Ext.Ajax.request({
 					   	  url : basePath +dealurl,
 					   	  params :{
 					   		  id:boid,
 					   		  data:unescape(Ext.JSON.encode(dd)),
 					   		  caller:caller
 					   		  },
 					   	  method : 'post',
 					   	  callback : function(options,success,response){
 					   		var localJson = new Ext.decode(response.responseText);
 					   		if(localJson.success){
 			    				Ext.Msg.alert('提示',title+'成功!',function(){
 			    					Ext.getCmp('win').close();
 			    					window.location.reload();
 			    				});
 				   			} else if(localJson.exceptionInfo){
 				   				var str = localJson.exceptionInfo;
 				   					showError(str);
 					   				return;    					   			
 					   	 } else{
 				   				saveFailure();
 				   			}
 					   	  }
 					   });
 					   
 				   }
 				}
 			},{
 				xtype:'button',
 				columnWidth:0.1,
 				text:'关闭',
 				width:60,
 				iconCls: 'x-button-icon-close',
 				margin:'0 0 0 10',
 				handler:function(btn){
 					Ext.getCmp('win').close();
 				}
 			}]
        }).show();
	}
});