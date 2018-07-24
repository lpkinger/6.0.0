Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.Tender', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:['scm.purchase.Tender','scm.purchase.TenderFormPanel','scm.purchase.TenderProductGridPanel',
    		'scm.purchase.TenderSupplierGridPanel','core.button.ResSubmit','core.form.Panel','core.toolbar.Toolbar',
    		'core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Save','core.button.Close','core.button.Update',
    		'core.button.Delete','core.form.FileField2','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
    ],
    init:function(){
    	var me = this;
    	this.control({
    		'gridpanel': {
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record);
    			}
    		},
    		'dbfindtrigger[name=contact]':{
    			focus:function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('supplierGrid').selModel.lastSelected;
    				var uu = record.data['uu'];
    				if(!uu){
    					showError("请先选择供应商!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}else{
    					 t.dbBaseCondition = "ve_uu='" + uu + "'";
    				}
    			}
    		},
            'field[name=currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pt_indate]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'radiogroup[id=ifOpen]':{
    			change:function(self,newVal,oldVal){
	        		if(typeof(newVal.ifOpen)=='number'){
		        		if(newVal.ifOpen==1){
		        			Ext.getCmp('supplierGrid').tab.hide();
		        			var tab = Ext.getCmp('tab');
		        			if(tab.getActiveTab().id!='productGrid'){
		        				tab.setActiveTab('productGrid');
		        			}
		        		}else{
		        			Ext.getCmp('supplierGrid').tab.show();
		        			Ext.getCmp('tab').setActiveTab('supplierGrid');
		        		}		        			
	        		}
	        	}
    		},
    		'field[id=currency]': {
    			afterrender:function(field){
    				var val = field.value;
    				var ifTax = Ext.getCmp('ifTax');
    				var invoiceType = Ext.getCmp('invoiceType');
    				if(val!='RMB'){
    					ifTax.setValue(0);
    					ifTax.setReadOnly(true);
    					ifTax.setFieldStyle('background:#eeeeee');
    					invoiceType.setValue(0);
    					invoiceType.setReadOnly(true);
    					invoiceType.setFieldStyle('background:#eeeeee');
    				}
    			},
    			change:function(field,newVal,oldVal){
    				var ifTax = Ext.getCmp('ifTax');
    				var invoiceType = Ext.getCmp('invoiceType');
    				if(newVal!='RMB'){
    					ifTax.setValue(0);
    					ifTax.setReadOnly(true);
    					ifTax.setFieldStyle('background:#eeeeee');
    					invoiceType.setValue(0);
    					invoiceType.setReadOnly(true);
    					invoiceType.setFieldStyle('background:#eeeeee');
    				}else{
    					ifTax.setReadOnly(false);
    					ifTax.setFieldStyle('background:#FFFAFA');
    					invoiceType.setReadOnly(false);
    					invoiceType.setFieldStyle('background:#FFFAFA');
    				}
    			}
    		},
    		'erpAddButton': {
    			afterrender:function(btn){
	            	var formdata = btn.up('form').formdata;
    				if(formdata){
    					btn.show();
    				}
            	},
                click: function() {
                    me.FormUtil.onAdd('Tender', '招标单', 'jsps/scm/purchase/tender.jsp');
                }
            },
    		'erpSaveButton': {
    			afterrender:function(btn){
    				var formdata = btn.up('form').formdata;
    				if(formdata){
    					btn.hide();
    				}
    			},
                click: function(btn) {
                	var form = me.getForm(btn);
                	var grids = Ext.ComponentQuery.query('gridpanel');
                	Ext.Array.each(grids,function(grid){
                		if(!grid.isHidden()){
                			me.getUnFinish(grid,form,false,0);
                		}
                	});
                }
            },
            'erpDeleteButton': {
            	afterrender:function(btn){
	            	var formdata = btn.up('form').formdata;
    				if(formdata&&formdata.tt_statuscode&&formdata.tt_statuscode=='ENTERING'){
    					btn.show();
    				}
            	},
                click: function(btn) {
                    me.FormUtil.onDelete(Ext.getCmp('id').value);
                }
            },
            'erpUpdateButton': {
            	afterrender:function(btn){
	            	var formdata = btn.up('form').formdata;
    				if(formdata&&formdata.tt_statuscode&&formdata.tt_statuscode=='ENTERING'){
    					btn.show();
    				}
            	},
                click: function(btn) {
                	var form = me.getForm(btn);
                	var grids = Ext.ComponentQuery.query('gridpanel');
                	Ext.Array.each(grids,function(grid){
                		if(!grid.isHidden()){
                			me.getUnFinish(grid,form,true,0);
                		}
                	});
                }
            },
            'erpResSubmitButton':{
            	afterrender:function(btn){
	            	var formdata = btn.up('form').formdata;

    				if(formdata&&formdata.tt_statuscode&&formdata.tt_statuscode=='COMMITED'){
    					btn.show();
    				}
            	},
                click: function(btn) {
                var formdata = btn.up('form').formdata;
                	me.FormUtil.onResSubmit(formdata.id);
                }
            },
            '#release':{
            	afterrender:function(btn){
	            	var formdata = btn.up('form').formdata;
    				if(formdata&&(formdata.overdue==1||(formdata.tt_statuscode&&formdata.tt_statuscode!='ENTERING'))){
    					btn.hide();
    				}
            	},
        	  	click: function(btn) {
            		var form = me.getForm(btn);
            		var grids = Ext.ComponentQuery.query('gridpanel');
                	Ext.Array.each(grids,function(grid){
                		if(!grid.isHidden()){
                			if(Ext.isEmpty(formCondition)){
		            			me.getUnFinish(grid,form,false,1);
		            		}else{
		            			me.getUnFinish(grid,form,true,1);
		            		}
                		}
                	});
            	}
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            '#deletedetail':{
            	click:function(btn){
            		var grid = btn.ownerCt.ownerCt;
            		var record = grid.selModel.lastSelected;
            		if(!Ext.isEmpty(formCondition)&&!record.dirty){
            			me.deleteDetail(record,grid);
            		}else{
            			grid.store.remove(record);
            		}
            	}
            }
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
		var me = this;
		var grid = selModel.ownerCt;
		if(grid && !grid.readOnly && !grid.NoAdd){
			var index = grid.store.indexOf(record);
			if(index == grid.store.indexOf(grid.store.last())){
				var detno = parseInt(record.data['index']);
				var data = getEmptyData(detno+1);//就再加10行
				grid.store.loadData(data,true);
			}
			var btn = grid.down('#deletedetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('copydetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('pastedetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('updetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('downdetail');
			if(btn)
				btn.setDisabled(false);
			if(grid.down('tbtext[name=row]')){
				grid.down('tbtext[name=row]').setText(index+1);
			}
	    }
	},
	beforeSave: function(form,update,isPublish,issubmit){
		var me = this;
		var url = form.saveUrl;
		var params = new Object();
		var questionEndDate = Ext.getCmp('questionEndDate').value;
		var endDate = Ext.getCmp('endDate').value;
		var publishDate = Ext.getCmp('publishDate').value;
		if(!Ext.isEmpty(questionEndDate)&&Ext.Date.format(questionEndDate,'Y-m-d')>=Ext.Date.format(endDate,'Y-m-d')){
			showError('提问截止时间必须小于投标截止时间!');
			return;
		}
		if(Ext.Date.format(endDate,'Y-m-d')<Ext.Date.format(new Date(),'Y-m-d')){
			showError('投标截止时间必须大于等于当前时间!');
			return;
		}
		if(Ext.Date.format(endDate,'Y-m-d')>=Ext.Date.format(publishDate,'Y-m-d')){
			showError('公布结果时间必须大于投标截止时间！');
			return;
		}
		var s = me.FormUtil.checkFormDirty(form);
		var grid = Ext.getCmp('productGrid');
		var param = me.GridUtil.getGridStore(grid);
		var param3 = me.getGridStore(grid);
		if(param3.length<1){
			showError('请选择要招标的产品！');
			return;
		}
		var formStore = me.getFormStore(form);
		var grid1 = Ext.getCmp('supplierGrid');
		var param1 =new Array();
		if(formStore['ifOpen']==0){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		
		if(!isPublish&&update&&!s&&param.length<1&&param1.length<1){
			showError('未修改数据！');
			return;
		}
		
		params.formStore=unescape(escape(Ext.JSON.encode(formStore)));
		if(param.length>0){
			params.param = unescape("[" + param.toString() + "]");
		}
		var param2 = me.getGridStore(grid1);
		if(formStore['ifOpen']==0&&param2.length<1){
			showError('指定招标,请先选择供应商！');
			return;
		}
		if(param2.length>0){
			params.param1 = unescape("[" + param2.toString() + "]");
		}
		params.caller = caller;
		params.isPublish = isPublish;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/saveorPublicTender.action',
			params : params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					if(!update){
						id = localJson.id;
					}
					if(isPublish==1){
						if(issubmit){
							me.beforePublish(update);
						}else{
							Ext.Msg.alert($I18N.common.msg.title_prompt, '发布成功', function(){
								window.location.href = 'tenderEstimate.jsp?formCondition=idIS' + id;
							});
						}
					}else{
						saveSuccess(function(){
							//add成功后刷新页面进入可编辑的页面 
							if(contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=idIS' + id;
							} else {
								window.location.href = window.location.href + '?formCondition=idIS' + id;
							}
						});
					}
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						if(!update){
							id = localJson.id;
						}
						if(isPublish==1){
							if(!issubmit){
								Ext.Msg.alert($I18N.common.msg.title_prompt, '发布成功', function(){
									window.location.href = 'tenderEstimate.jsp?formCondition=idIS' + id;
								});
							}
						}
					}
					showError(str);
					return;
				} else{
					saveFailure();//@i18n/i18n.js
				}
			}
		});
	},
	getFormStore:function(form){
		var r = form.getValues();
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
			if(f && f.xtype == 'checkboxgroup'){
				var values = '';
				Ext.Array.each(r[f.name],function(value){
					if(value!='0'){
						values +=','+value;
					}
				});
				r[f.name] = values.substring(1);
			}
		});
		return r;
	},
	deleteDetail: function(record,grid){
		var url,params=new Object();
		if(grid.id=='productGrid'){
			url = 'scm/purchase/deleteProd.action';
			params.tenderProdId = record.data['id'];
		}else{
			url = 'scm/purchase/removeSaleTender.action';
			params.id = id;
			params.vendUU = record.data['uu'];
			params.caller = caller;
		}
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					grid.store.remove(record);
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				} 
			}
		});
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.GridUtil.isBlank(grid, data)){
					Ext.each(grid.columns, function(c){
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
	getUnFinish:function(grid,form,update,isPublish){
		var me = this;
		var errInfo = me.GridUtil.getUnFinish(grid);
		var issubmit = false;
		if(isPublish==1){
			issubmit = me.isSubmit();
		}
		if(errInfo.length > 0){
			errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
			warnMsg(errInfo, function(btn){
				if(btn == 'yes'){
					if(issubmit){
						if(!update){
							me.beforeSave(form,update,isPublish,issubmit);
						}
					}else{
						me.beforeSave(form,update,isPublish);
					}
				} else {
					return;
				}
			});
		}else{
			if(issubmit){
				if(!update){
					me.beforeSave(form,update,isPublish,issubmit);
				}else{
					me.beforePublish(update);
				}
			}else{
				me.beforeSave(form,update,isPublish);
			}
		}
	},
	isSubmit: function(){
		var me = this,isSubmit = true;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/isSubmit.action',
			params : {
				caller : caller
			},
			method : 'post',
			async : false,
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					isSubmit = localJson.submit;
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
			}
		});
		return isSubmit;
	},
	beforePublish: function(update){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/publicTender.action',
			params : {
				id : id,
				caller : caller
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					me.getMultiAssigns(id, caller,form,update);
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
			}
		});
	},
	getMultiAssigns:function(id,caller,form,update){
		var me = this;
		Ext.Ajax.request({
			url : basePath + '/common/getMultiNodeAssigns.action',
			params: {
				id: id,
				caller:caller
			},
			method : 'post',
			callback : function(){
				var localJson = new Ext.decode(arguments[2].responseText); 
				if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					showError(str);
				}else {
					if(localJson.MultiAssign){
						if(localJson.autoSetJnode){
							form.SetNodeDealMan(id);
						}else me.showAssignWin(localJson.assigns,id,caller,form,update);
					}else {
						showMessage('提示', '提交成功!', 1000);
						if(update){
							window.location.reload();
						}else{
							if(contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=idIS' + id;
							} else {
								window.location.href = window.location.href + '?formCondition=idIS' + id;
							}
						}
						if(form.onSumitSuccess){
							form.onSumitSuccess();
						}
					}
				}
			},
			scope:this
		});
	},
	showAssignWin :function(persons,id,caller,form,update){
		var me=this;
		var confirm = new Ext.button.Button({
			text:$I18N.common.button.erpConfirmButton,
			handler:function(btn){
				var panels = Ext.ComponentQuery.query('window >tabpanel>panel');
				var params = new Array(), param = new Object(),flag=0;
				/**调整为必须提交人指定节点处理人，无需默认*/
				Ext.each(panels,function(panel){
					if(panel){
						flag=0;
						Ext.Array.each(panel.items.items,function(item){
							if(item.getValue()){
								param=new Object(),label = item.boxLabel,em_code = label.substring(label.lastIndexOf('(')+1,label.length-1);
								param.em_code = em_code;
								param.nodeId=item.name;
								params.push(JSON.stringify(param));	
								flag=1;
								return false;
							}							
						});
					   if(flag==0) {
						   showError('节点【'+panel.title+'】未指定处理人!');
						   return false;
					   }	
					}
				});
				if(flag==0) return;
				Ext.Ajax.request({
					url: basePath + 'common/takeOverTask.action',
					async: false,
					params: {
						params:unescape(params),
						_noc: 1
					},
					callback: function(options,success,response){
						var text = response.responseText;
						jsonData = Ext.decode(text);
						//再发送请求 
						if(jsonData.success){							
							//如果流程处理人选择自己，则跳过，选择下一节点处理人
							me.getMultiAssigns(id, caller, form,update);								

							win.close();
							Ext.Msg.alert('提示' ,"指派成功!");
						}else{
							Ext.Msg.alert('提示' ,"指派失败!");
							win.close();
						}
					}
				});
			}
		});
		var cancel = new Ext.button.Button({
			text:$I18N.common.button.erpCancelButton,	
			handler:function(){
				win.close();
				if(update){
					window.location.reload();
				}else{
					if(contains(window.location.href, '?', true)){
						window.location.href = window.location.href + '&formCondition=idIS' + id;
					} else {
						window.location.href = window.location.href + '?formCondition=idIS' + id;
					}
				}
			}
		});
		var searchKey = new Object();
		var win = Ext.create('Ext.window.Window', {
			title:'<div align="center">节点处理人</div>',
			height: 450,
			width: 650,
			layout:'border',
			closable:false,
			modal:true,
			id:'win',
			autoScroll:true,
			buttonAlign:'center',
			buttons: [confirm,cancel],
			items: []    	   
		});
		win.add([{
			xtype:'textfield',
			margin:'10 20 10 20',
			fieldLabel:'快速搜索',
			labelStyle:'font-weight:bold;',
			id:'searchtextfield',
			enableKeyEvents:true,
			region:'north',
			listeners:{
				keydown:function(field,e){
					if(e.getKey()==Ext.EventObject.ENTER){	
						searchKey[Ext.getCmp('processTab').getActiveTab().id]=field.value;
						var results=Ext.Array.filter(persons[Ext.getCmp('processTab').getActiveTab().id].JP_CANDIDATES,function(JP_CANDIDATE){
							if(field.value==undefined || JP_CANDIDATE.indexOf(field.value)!=-1) return JP_CANDIDATE;
						});
						Ext.Array.each(Ext.getCmp('processTab').getActiveTab().personUsers,function(item){
							Ext.getCmp('processTab').getActiveTab().remove(item);
						});						
						me.FormUtil.addUserItems(Ext.getCmp('processTab').getActiveTab(),persons[Ext.getCmp('processTab').getActiveTab().id].JP_NODEID,results);

					}
				}
			}
		}]);
		me.FormUtil.addAssignItems(win,persons,searchKey);
		win.show();
	}
});