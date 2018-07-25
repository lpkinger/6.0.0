Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.Check', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.Check','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','pm.bom.ECRChangeGrid',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.PrintByCondition',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.TurnECN','core.button.ResEnd','core.button.End',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.grid.YnColumn','core.button.Print', 'core.button.Sync'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			/*afterrender: function(btn){
    				console.log(Ext.getCmp('grid'));
    				Ext.getCmp('grid').readOnly = false;
    			},*/
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			},
    			beforeedit:function(e){
    				//标准字段
    				var old_dataIndex = ["ecrd_isbatch","ecrd_type","ecrd_bomid","ecrd_mothercode","ecrd_mothername","ecrd_motherspec","ecrd_bddetno","ecrd_soncode","ecrd_sonname","ecrd_sonspec","ecrd_oldforreplace","ecrd_oldbaseqty","ecrd_newbaseqty","ECRD_OLDLOCATION","ecrd_location","ecrd_bdremark","ecrd_repname","ecrd_repspec","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal","ecrd_remark","ecrd_beforechange","ecrd_afterchange","ecrd_ecrid"];
					//记录差集字段
					var difference_dataIndex = [];
					var ecrd_type = e.record.data.ecrd_type;
					var g=e.grid,r=e.record,f=e.field;
					//获取当前字段
					var dataIndex = [];
					Ext.each(g.columns,function(column){
						dataIndex.push(column.dataIndex);
					});
					difference_dataIndex = Ext.Array.difference(dataIndex,old_dataIndex);
					if(ecrd_type=="DISABLE"){
						var edit_arr = ["ecrd_type","ecrd_bomid","ecrd_bddetno","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal","ecrd_remark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="ADD"){
						var edit_arr = ["ecrd_type","ecrd_bomid","ecrd_soncode","ecrd_newbaseqty","ecrd_location","ecrd_bdremark","ecrd_remark","ecrd_beforechange","ecrd_afterchange","ecrd_repcode"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="CHANGENAME"){
						var edit_arr = ["ecrd_type","ecrd_soncode","ecrd_sonname","ecrd_sonspec"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="UPDATE"){
						var edit_arr = ["ecrd_type","ecrd_bomid","ecrd_bddetno","ecrd_newbaseqty","ECRD_OLDLOCATION","ecrd_location","ecrd_remark","ecrd_bdremark","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="SWITCH"){
						var edit_arr = ["ecrd_type","ecrd_bomid","ecrd_bddetno","ecrd_soncode","ecrd_newbaseqty","ecrd_location","ecrd_oldforreplace","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal","ecrd_remark","ECRD_OLDLOCATION","ecrd_bdremark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="REPADD"){
						var edit_arr = ["ecrd_type","ecrd_soncode","ecrd_bomid","ecrd_bddetno","ecrd_repcode","ecrd_bdremark","ecrd_remark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="REPDISABLE"){
						var edit_arr = ["ecrd_type","ecrd_bomid","ecrd_bddetno","ecrd_repcode","ecrd_bdremark","ecrd_remark","ecrd_beforechange","ecrd_afterchange","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="BATCHDISABLE"){
						var edit_arr = ["ecrd_type","ecrd_soncode","ecrd_bdremark","ecrd_remark","ecrd_beforechange","ecrd_afterchange","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="BATCHSWITCH"){
						var edit_arr = ["ecrd_type","ecrd_soncode","ecrd_oldforreplace","ecrd_repcode","ecrd_oldproddeal","ecrd_semisdeal","ecrd_endproddeal","ecrd_bdremark","ecrd_remark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="BATCHREPADD"){
						var edit_arr = ["ecrd_type","ecrd_soncode","ecrd_repcode","ecrd_remark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="BATCHREPDISABLE"){
						var edit_arr = ["ecrd_soncode","ecrd_type","ecrd_repcode","ecrd_remark","ecrd_beforechange","ecrd_afterchange"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ecrd_type=="BATCHSETMAIN"){
						var edit_arr = ["ecrd_type","ecrd_soncode"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}
					if(g.binds){
						var bool=true;
						Ext.Array.each(g.binds,function(item){
							if(Ext.Array.contains(item.fields,f)){
								Ext.each(item.refFields,function(field){
									if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0'){
										bool=false;
									} 
								});							
							} 
						});
						return bool;
					}
				}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				
    			}
    		},
    		'textareafield[name=ecr_tempb]':{
				beforerender:function(field){
					field.labelAlign='top';
					field.height=250;
					field.fieldStyle=field.fieldStyle+';font-weight:700;font-color:#0A0A0A;';
				}
			},
			'textareafield[name=ecr_tempc]':{
				beforerender:function(field){
					field.labelAlign='top';
					field.height=250;
					field.fieldStyle=field.fieldStyle+';font-weight:700;font-color:#0A0A0A;';
				}
			},
    		'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//默认取第一行的旧料处理方式，半成品处理方式，成品处理方式
    				this.setDefault();
					this.FormUtil.beforeSave(this);
				}
			},
			'erpEndButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
				click :function(btn){
					var me = this;
					Ext.MessageBox.confirm('结案提示', '确认结案?',function(btn){
				  		if(btn=='yes'){
				  			me.FormUtil.onEnd(Ext.getCmp('ecr_id').value);
				  		}else{
	   						return;
	   					}
					});
				}
			},
			'erpResEndButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click :function(btn){
					this.FormUtil.onResEnd(Ext.getCmp('ecr_id').value);
				}
			},
			'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}/*,
    			afterrender:function(btn){
    				var form=btn.ownerCt.ownerCt;
    				var bool=false;
    				var insertId=0;
    				var keys=form.items.keys;
    				Ext.Array.each(keys,function(key,index){
    					if(key=='ecr_tempc'){
    						insertId=index;
    						bool=true;
    						return;
    					}
    				});
    				if(bool){
    					var tempb=Ext.getCmp('ecr_tempb').value;
    					var tempc=Ext.getCmp('ecr_tempc').value;  	
    					form.insert(insertId+1,{
        					title:'变更前后',
        					columnWidth:1,
        					griddata:{
        						tempb:tempb,
        						tempc:tempc
        					},
        					xtype:'erpECRChangeGridPanel'
        				});
    				}    				
    			}*/
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				//如果为空设置默认旧料处理方式
    				this.setDefault();
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCheck', '新增ECR资料', 'jsps/pm/bom/check.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.onSubmit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="EcrAudit";
				var condition='{ECR.ecr_id}='+Ext.getCmp('ecr_id').value+'';
				var id=Ext.getCmp('ecr_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ecr_id').value);
    			}
    		}, 
			'erpTurnECNButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatus2code');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入工程变更单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/bom/turnECN.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ecr_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){  	    		    				
    	    		    				var id = localJson.id;
    	    		    				if (id && id>0){
    	    		    					if(localJson.error != null && localJson.error != ""){
    	    		    					   showMessage("提示", localJson.error);
    	    		    					   window.location.reload();
    	    		    				    }
	    	    		    				turnSuccess(function(){
	    	    		    					var url = "jsps/pm/bom/ECN.jsp?formCondition=ecn_idIS" + id + "&gridCondition=ed_ecnidIS"+id;
	        	    		    				me.FormUtil.onAdd('ECN' + id, '工程变更单' + id, url);	    	    		    	
	    	    		    				});
    	    		    				}else{
    	    		    					if(localJson.error != null && localJson.error != ""){
    	    		    				       showError(localJson.error+",此单据不需要转ECN!"); 
    	    		    					   window.location.reload();
    	    		    				    } 	    		    					
    	    		    				}
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'combo[name=ecrd_type]':{
    			change:function(t, newValue, oldValue, eOpts ){
					var record = Ext.getCmp('grid').selModel.getLastSelected(); 
    				if(newValue!='' && newValue!=null ){
    					if(newValue.indexOf("BATCH")!=-1 && record.get('ecrd_isbatch')!=-1 ){
    						record.set('ecrd_isbatch',-1); 
    					}else if(newValue.indexOf("BATCH")== -1 && record.get('ecrd_isbatch')!=0){
    						record.set('ecrd_isbatch',0); 
    					} 
    				}
	    			/*var arr = Object.keys(record.data);
	    			Ext.each(arr,function(key,index){
	    				if(key!=t.name && key!='ecrd_detno'&&key!='ecrd_id'&&key!='ecrd_isbatch'){
	    					record.data[key]='';
	    				}
	    			});*/
				}
			}, 
    		'dbfindtrigger[name=ecrd_bomid]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected(); 
    				var type = record.data['ecrd_type']; 
    				if (type=='' || type == null){
    					showError("请先选择操作类型 !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}else{ 
        				if(type.indexOf("BATCH")!=-1){//需指定BOM
        					showError("批量操作不能指定BOM!");
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        				}
        				if(type =='CHANGENAME'){
        					showError("变更描述不需要指定BOM!");
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        				}
    				}
    			}
    		},
    		'dbfindtrigger[name=ecrd_bddetno]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false); 
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var bomid = record.data['ecrd_bomid'];
    				var type = record.data['ecrd_type']; 
    				if (type=='' || type == null){
    					showError("请先选择操作类型 !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}else{
    					if( type.indexOf("BATCH")!=-1 ){//批量处理，不需要指定BOM和序号
    						showError("批量处理不需要指定BOM和序号 !");
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        					return;
    					}else{//非批量处理
    						if(bomid=='' || bomid ==null) {
        						if( type.indexOf("BATCH")==-1 ){//非批量处理，需指定BOM
        							showError("请先选择BOM编号 !");
                					t.setHideTrigger(true);
                					t.setReadOnly(true);
                					return;
        						} 
        					}
            				if(type=="ADD" ){//需指定BOM 序号
            					showError("增加物料不需要选择BOM序号!");
            					t.setHideTrigger(true);
            					t.setReadOnly(true);
            					return;
            				}
    						
    					} 
    				}
    				t.dbBaseCondition = "bd_bomid='" + bomid + "'";
    			},
    			aftertrigger:function(t){
    				var record = Ext.getCmp('grid').selModel.getLastSelected(); 
    				var data=record.data;    	
    				var type = record.data['ecrd_type'];   
    				if (type.indexOf("SWITCH")!=-1){ 
    					record.set('ecrd_repcode',data['ecrd_soncode']); 
    					if(record.data['ecrd_sonname']){
    						record.set('ecrd_repname',data['ecrd_sonname']);
    					    record.set('ecrd_repspec',data['ecrd_sonspec']); 
    					    record.set('ecrd_replevel',data['pr_level']);
    						record.set('ecrd_sonname',null);
    					    record.set('ecrd_sonspec',null);
    					    record.set('ecrd_sonlevel',null);
    					    record.set('pr_level',null);
    					}else if (record.data['pr_detail']){
    						record.set('ecrd_repname',data['pr_detail']); 
    					    record.set('ecrd_repspec',data['pr_spec']); 
    					    record.set('ecrd_replevel',data['pr_level'])
    						record.set('pr_detail',null);
    					    record.set('pr_spec',null);
    					    record.set('pr_level',null);
    					}   	
    					record.set('ecrd_soncode',null); 
    					record.set('ecrd_newbaseqty',data['ecrd_oldbaseqty']);
    					record.set('ecrd_newbuildfinishqty',data['ecrd_oldbuildfinishqty']);
    				}    				
    			}
    		},
    		'dbfindtrigger[name=ecrd_soncode]': {
    			aftertrigger:function(t){
    				var record = Ext.getCmp('grid').selModel.getLastSelected(); 
    				var data = record.data;    	
    				var type = record.data['ecrd_type'];   
    				if (type.indexOf("CHANGENAME")!=-1){  						  					  					
    					if(record.data['ecrd_repname'] == '' || record.data['ecrd_repname'] == null){
    						//ed_repname为空则返回ecrd_sonname字段
    						record.set('ecrd_repname',data['ecrd_sonname']);
    					}   									
    					if(record.data['ecrd_repspec'] == '' || record.data['ecrd_repspec'] == null){
    						//ed_repspec空则返回ecrd_sonspec
    						record.set('ecrd_repspec',data['ecrd_sonspec']);//如果为空的话则把子件规格的值给它
    					}
    				}   				
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#ecr_checkstatus2code');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            },
            'dbfindtrigger[name=ecrd_soncode]': {
            	focus: function(t){
            		var record = Ext.getCmp('grid').selModel.getLastSelected();
            		var type = record.data['ecrd_type']; 
    				if(type == 'DISABLE' || type == 'UPDATE' || type == 'REPDISABLE'){
            			t.setHideTrigger(true);
    					t.setReadOnly(true);
            		}else{
    					t.setHideTrigger(false);
    					t.setReadOnly(false);
    				}
            	}
            },
            'dbfindtrigger[name=ecrd_repcode]': {
            	focus: function(t){
            		var record = Ext.getCmp('grid').selModel.getLastSelected();
            		var type = record.data['ecrd_type']; 
    				if(type == 'SWITCH'){
            			t.setHideTrigger(true);
    					t.setReadOnly(true);
            		}else{
    					t.setHideTrigger(false);
    					t.setReadOnly(false);
    				}
            	}
            }
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onSubmit: function(id){ 
		var me=this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.FormUtil.checkFormDirty(form);
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);
				/*
				if(param == null || param == ''){
						if (Ext.getCmp('ecr_newprodname').value==''|| Ext.getCmp('ecr_newspec').value==''){
							showError("明细表还未添加数据,无法提交!");
							return;
						} 
					}  
					*/
				Ext.each(grids, function(grid, index){
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
			}
			if(s == '' || s == '<br/>'){
				me.FormUtil.submit(id);
			} else {
				Ext.MessageBox.show({
				     title:'保存修改?',
				     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
				     buttons: Ext.Msg.YESNOCANCEL,
				     icon: Ext.Msg.WARNING,
				     fn: function(btn){
				    	 if(btn == 'yes'){
				    		 me.FormUtil.onUpdate(form);
				    	 } else if(btn == 'no'){
				    		 me.FormUtil.submit(id);	
				    	 } else {
				    		 return;
				    	 }
				     }
				});
			}
		} else {
			me.FormUtil.checkForm();
		}
	},
	setDefault:function(){
		 var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		  var firstItem = grid.store.getAt(0);
		   if(firstItem) {
			   var oldprod = firstItem.get('ecrd_oldproddeal'), semis = firstItem.get('ecrd_semisdeal'),
			   	   endprod = firstItem.get('ecrd_endproddeal');
			   Ext.Array.each(items, function(item){
				   if(!Ext.isEmpty(item.data['ecrd_bomid'])){
					   if(Ext.isEmpty(item.data['ecrd_oldproddeal'])){
						   item.set('ecrd_oldproddeal', oldprod); 
					   }
					   if(Ext.isEmpty(item.data['ecrd_semisdeal'])){
						   item.set('ecrd_semisdeal', semis); 
					   } 
					   if(Ext.isEmpty(item.data['ecrd_endproddeal'])){
						   item.set('ecrd_endproddeal', endprod); 
					   } 
				   } 
			   });
		   }
	   }
});