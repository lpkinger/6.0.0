Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ECN', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.ECN','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Print','core.button.Delete','core.form.YnField','core.button.ECNCheck',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.grid.YnColumn','core.button.ExecuteECNAuto',
    		'core.button.Sync','core.button.CloseECNAllDetail','core.button.OpenECNAllDetail','core.button.TurnAutoECN','core.form.HrOrgSelectField',
    		'core.form.MultiField','core.button.AutoNewProd','core.button.ImportExcel','core.button.TurnApplication'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpImportExcelButton [id=excelfile]':{
    			change:function(field){
    					me.upload(field.ownerCt, field);
    				}
    		},
    		'#filebutton':{
    			afterrender:function(btn){
    					btn.setText("导入ECN");
    					btn.setHeight(22);
    				}
    		},
    		/*'field[name=ed_type]':{
    			change:function(f,newValue,oldValue,eOpts){
    				console.log(newValue);
    				console.log(oldValue);
    				console.log(f.value);//ed_boid  ed_mothercode ed_mothername ed_motherspec ed_oldproddeal ed_oldvenddeal
    				if(oldValue != ''){
    					var grid = Ext.getCmp('grid');
        				var record=Ext.getCmp('grid').selModel.getLastSelected();
        				var arr = Object.keys(record.data);
        				Ext.each(arr,function(key,index){
        					if(key!=f.name && key!='ed_detno'&&key!='ed_id'){
        						record.data[key]='';
        					}
        				});
    				}
    				
    			}
    		},*/
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){ 
    				Ext.getCmp('closedetail').setDisabled(false);
    				Ext.getCmp('opendetail').setDisabled(false);
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
		    	afterrender: function(btn){
					var status = Ext.getCmp('ecn_checkstatuscode');
					if(status && status.value != 'AUDITED'){
						Ext.getCmp('ExecuteECNAutobutton').hide();
					}
				},
				beforeedit:function(e){
					var ed_type = e.record.data.ed_type;
					var g=e.grid,r=e.record,f=e.field;
					//标准字段
    				var old_dataIndex = ["ed_code","ed_detno","ed_type","ed_ecnid","ed_isbatch","ed_boid","ed_mothercode","ed_mothername","ed_motherspec","ed_bddetno","ed_soncode","ed_oldforreplace","ed_oldbaseqty","ed_newbaseqty","ed_oldlocation","ed_location","ed_repcode","ed_oldproddeal","ed_oldvenddeal","ed_remark","ed_didstatus","ed_diddate","ed_didreason"];
					//记录差集字段
					var difference_dataIndex = [];
					//获取当前字段
					var dataIndex = [];
					Ext.each(g.columns,function(column){
						dataIndex.push(column.dataIndex);
					});
					difference_dataIndex = Ext.Array.difference(dataIndex,old_dataIndex);
					if(ed_type=="DISABLE"){
						var edit_arr = ["ed_type","ed_boid","ed_bddetno","ed_oldproddeal","ed_oldvenddeal","ed_remark"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ed_type=="ADD"){
						//需要增加可编辑的字段添加到这个数组
						var edit_arr = ["ed_oldproddeal","ed_oldvenddeal","ed_type","ed_boid","ed_soncode","ed_newbaseqty","ed_location","ed_bdremark","ed_remark","ed_repcode"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ed_type=="UPDATE"){
						var edit_arr = ["ed_type","ed_boid","ed_bddetno","ed_newbaseqty","ed_location","ed_oldlocation","ed_bdremark","ed_oldproddeal","ed_oldvenddeal","ed_remark"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ed_type=="SWITCH"){
						var edit_arr = ["ed_type","ed_boid","ed_bddetno","ed_soncode","ed_newbaseqty","ed_location","ed_remark","ed_oldproddeal","ed_oldvenddeal","ed_oldlocation"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ed_type=="REPDISABLE"){
						var edit_arr = ["ed_type","ed_boid","ed_bddetno","ed_repcode","ed_remark","ed_oldproddeal","ed_oldvenddeal"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}else if(ed_type=="REPADD"){
						var edit_arr = ["ed_type","ed_boid","ed_soncode","ed_bddetno","ed_repcode","ed_remark"];
						edit_arr = Ext.Array.merge(edit_arr,difference_dataIndex);
						if(edit_arr.indexOf(f)!=-1){
							return true;
						}else{
							return false;
						}
					}
				}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){ 
    				btn.ownerCt.add({
    					xtype:'button',
    					text:'关闭明细行',
    					width:120,
    					iconCls: 'x-button-icon-check',
    			    	cls: 'x-btn-gray',
    					id:'closedetail',
    					style: {
    			    		marginLeft: '10px'
    			        },
    			        disabled:true,
    				    handler:function(){
    				        var grid=Ext.getCmp('grid');
    	    				var record=grid.getSelectionModel().getLastSelected(); 
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "/pm/bom/closeECNDetail.action",
    	    		         	params:{
    	    		         	  id:record.data.ed_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','明细行关闭成功!'); 
    	    	        				var id = Ext.getCmp('ecn_id').value;
    	    	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:"ed_ecnid="+id});
    	    	        				var form = Ext.getCmp('form');
    	    	        				me.FormUtil.loadNewStore(form,{caller: caller, condition: "ecn_id=" +id});
    	    		         		}
    	    		         	}
    	    		         });
    				    }		
    				});
    				btn.ownerCt.add({
    					xtype:'button',
    					text:'打开明细行',
    					width:120,
    					iconCls: 'x-button-icon-check',
    			    	cls: 'x-btn-gray',
    					id:'opendetail',
    					style: {
    			    		marginLeft: '10px'
    			        },
    			        disabled:true,
    				    handler:function(){
    				        var grid=Ext.getCmp('grid');
    	    				var record=grid.getSelectionModel().getLastSelected(); 
    	    				grid.setLoading(true);
    	    				Ext.Ajax.request({//拿到grid的columns
    	    		         	url : basePath + "/pm/bom/openECNDetail.action",
    	    		         	params:{
    	    		         	  id:record.data.ed_id
    	    		         	},
    	    		         	method : 'post',
    	    		         	callback : function(options,success,response){
    	    		         		grid.setLoading(false);
    	    		         		var res = new Ext.decode(response.responseText);
    	    		         		if(res.exceptionInfo){
    	    		         			showError(res.exceptionInfo);return;
    	    		         		}else if(res.success){
    	    		         			Ext.Msg.alert('提示','明细行打开成功!');
    	    		         			var id = Ext.getCmp('ecn_id').value;
    	    	        				me.GridUtil.loadNewStore(grid,{caller:caller,condition:"ed_ecnid="+id});
    	    	        				var form = Ext.getCmp('form');
    	    	        				me.FormUtil.loadNewStore(form,{caller: caller, condition: "ecn_id=" +id});
    	    		         		}
    	    		         	}
    	    		         });
    				    }		
    				});
    			}
    		},
    		'erpCloseECNAllDetailButton':{
    			click:function(btn){
    				var grid=Ext.getCmp('grid');   	    				
    	    		grid.setLoading(true);
    				Ext.Ajax.request({//关闭所有明细
    		         	url : basePath + "/pm/bom/closeECNAllDetail.action",
    		         	params:{
    		         	  id:Ext.getCmp("ecn_id").value,
    		         	  caller:caller
    		         	},
    		         	method : 'post',
    		         	callback : function(options,success,response){
    		         		grid.setLoading(false);
    		         		var res = new Ext.decode(response.responseText);
    		         		if(res.exceptionInfo){
    		         			showError(res.exceptionInfo);return;
    		         		}else if(res.success){
    		         			Ext.Msg.alert('提示','打开状态的所有明细关闭成功!'); 
    		         			var id = Ext.getCmp('ecn_id').value;
    		         			window.location.href = basePath + "jsps/pm/bom/ECN.jsp?formCondition=ecn_idIS" +id+"&gridCondition=ed_ecnidIS"+id;
    		         		}
    		         	}
    		         });
    			},
    			afterrender:function(btn){//整张单已执行不允许关闭
    				var didCode = Ext.getCmp("ecn_didstatuscode");
    				if(didCode && didCode.value == 'EXECUTED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpOpenECNAllDetailButton':{
    			click:function(btn){
    				var grid=Ext.getCmp('grid');   	    				
    	    		grid.setLoading(true);
    				Ext.Ajax.request({//拿到grid的columns
    		         	url : basePath + "/pm/bom/openECNAllDetail.action",
    		         	params:{
    		         	   id:Ext.getCmp("ecn_id").value,
    		         	   caller:caller
    		         	},
    		         	method : 'post',
    		         	callback : function(options,success,response){
    		         		grid.setLoading(false);
    		         		var res = new Ext.decode(response.responseText);
    		         		if(res.exceptionInfo){
    		         			showError(res.exceptionInfo);return;
    		         		}else if(res.success){
    		         			Ext.Msg.alert('提示','关闭状态的所有明细打开成功!'); 
    	        				var id = Ext.getCmp('ecn_id').value;
    		         			window.location.href = basePath + "jsps/pm/bom/ECN.jsp?formCondition=ecn_idIS" +id+"&gridCondition=ed_ecnidIS"+id;
    		         		}
    		         	}
    		         });
    			},
    			afterrender:function(btn){//整张单已执行不允许打开
    				var didCode = Ext.getCmp("ecn_didstatuscode");
    				if(didCode && didCode.value == 'EXECUTED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnAutoECNButton':{
    			click:function(btn){//转立即执行
    				var id = Ext.getCmp("ecn_id");
    				if(id && id.value != null && id.value != ''){
    				    me.turnAutoECN(id.value);
    				}
    			},
    			afterrender:function(btn){
    				//未执行已审核的自然变更才允许转立即执行
    				var didCode = Ext.getCmp("ecn_didstatuscode");
    				var statusCode = Ext.getCmp("ecn_checkstatuscode");
    				var type = Ext.getCmp("ecn_type");
    				if((didCode && didCode.value == 'EXECUTED') || (type && type.value != 'AUTO') || (statusCode&&statusCode.value !='AUDITED')){
    					btn.hide();
    				}
    			}
    		},
    		'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
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
    			click: function(btn){
    				var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ecn_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addECN', '新增ECN资料', 'jsps/pm/bom/ECN.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ecn_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ecn_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ecn_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ecn_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="EcnChange";
				var condition='{Ecn.ecn_id}='+Ext.getCmp('ecn_id').value+'';
				var id=Ext.getCmp('ecn_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},
    		'button[id=ExecuteECNAutobutton]': {
    			afterrender: function(btn){ 
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				var type = Ext.getCmp('ecn_type'); 
    				var didstatus = Ext.getCmp('ecn_didstatuscode'); 
    				if(status && status.value != 'AUDITED' ){
    					btn.hide();
    				}
    				if(type && type.value != 'AUTO' ){
    					btn.hide();
    				}
    				if(didstatus && didstatus.value != 'OPEN' ){
    					btn.hide();
    				}
    			}
    		},
    		'dbfindtrigger[name=ed_boid]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false); 
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
					var type = record.data['ed_type']; 
    				if (type=='' || type == null){ 
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					showError("请先选择操作类型 !");
    					return;
    				}
    			}
    		},
    		'dbfindtrigger[name=ed_bddetno]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false); 
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var bomid = record.data['ed_boid'];
    				var type = record.data['ed_type']; 
    				if (type=='' || type == null){
    					showError("请先选择操作类型 !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}else{
    					if(bomid=='' || bomid ==null) {
    						showError("请先选择BOM编号 !");
        					t.setHideTrigger(true);
        					t.setReadOnly(true);
        					return;
    					}
	    				if(type=="ADD" ){//需指定BOM 序号
	    					showError("增加物料不需要选择BOM序号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    					return;
	    				}
    				}
    				t.dbBaseCondition = "bd_bomid='" + bomid + "'";
    			},
    			aftertrigger:function(t){
    				var record = Ext.getCmp('grid').selModel.getLastSelected(); 
    				var data=record.data;    	
    				var type = record.data['ed_type'];   
    				if (type.indexOf("SWITCH")!=-1){  
    					record.set('ed_repcode',data['ed_soncode']);
    					record.set('ed_soncode',null); 
    					record.set('ed_sonname',null);
    					record.set('ed_sonspec',null); 
    					record.set('ed_repname',data['ed_sonname']);  
    					record.set('ed_repspec',data['ed_sonspec']); 
    					record.set('ed_newbaseqty',data['ed_oldbaseqty']);  //替换的变更，默认新单位用量等于旧单位用量
    					record.set('ed_newbuildfinishqty',data['ed_oldbuildfinishqty']);
    				}
    				
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#ecn_checkstatuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            },
            'dbfindtrigger[name=ed_soncode]': {
            	focus: function(t){
            		var record = Ext.getCmp('grid').selModel.getLastSelected();
            		var type = record.data['ed_type']; 
    				if(type == 'DISABLE' || type == 'UPDATE' || type == 'REPDISABLE' ){
            			t.setHideTrigger(true);
    					t.setReadOnly(true);
            		}else{
    					t.setHideTrigger(false);
    					t.setReadOnly(false);
    				}
            	}
            },
            'dbfindtrigger[name=ed_repcode]': {
            	focus: function(t){
            		var record = Ext.getCmp('grid').selModel.getLastSelected();
            		var type = record.data['ed_type']; 
    				if(type == 'SWITCH'){
            			t.setHideTrigger(true);
    					t.setReadOnly(true);
            		}else{
    					t.setHideTrigger(false);
    					t.setReadOnly(false);
    				}
            	}
            },
            'erpAutoNewProdButton':{ //自动生成料号
            	afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			},
            	click:function(b){
            		var id = Ext.getCmp("ecn_id");
    				if(id && id.value != null && id.value != ''){
    				   me.autoNewProd(id.value);
    				}
            	}
            },
             'erpTurnApplicationButton':{ //转请购单按钮
            	afterrender: function(btn){
    				var status = Ext.getCmp('ecn_checkstatuscode');
    				if(status && status.value != 'AUDITED' ){
    					btn.hide();
    				}
    			},
            	click:function(b){
            		var id = Ext.getCmp("ecn_id");
    				if(id && id.value != null && id.value != ''){
    				   me.turnApplication(id.value);
    				}
            	}
            }
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    upload:function(form, field){
		console.log("upload");
		var me = this;
		var fileType = ".xlsx|.xls|";
		var filePathValue = field.value;
		var v = filePathValue.substring(filePathValue.lastIndexOf(".")); 
		if (fileType.indexOf(v + "|") == -1) {  
            Ext.Msg.alert("提示", "您上传的文件格式不兼容，请选择excel格式！");  
            return;  
        }
		var grid_panel = Ext.getCmp('grid');
		form.getForm().submit({
			url:basePath + 'pm/bom/importECN.action',
			method : "POST",  
            async : true,   
            success : function(options,data) {
            	me.FormUtil.setLoading(false);
            	var json = eval("("+data.response.responseText+")");//将json类型字符串转换为json对象
            	console.log(data);
            	if(data.result.deptno!="null"){
            		showError(data.result.deptno);
            		window.location.href=window.location.href;
            	}else{
            		window.location.href=window.location.href+ '?formCondition=ecn_idIS' + 
            		json.ecn_id + '&gridCondition=ed_ecnidIS' + json.ecn_id;;
            	}
            },
            failure : function(form, data) {  
            	showError("数据有误(导入数据格式不正确)!");
                return;  
            }  
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turnAutoECN:function(id){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
		    url : basePath + 'pm/bom/turnAutoECN.action',
		    params: {
		    	caller:caller,
		        id: id		       
		    },
		    method : 'post',
		    callback : function(options,success,response){
		    	me.FormUtil.setLoading(false);
		   		var localJson = new Ext.decode(response.responseText);
		   		if(localJson.success){
		   			Ext.Msg.alert("提示","操作成功！");
		   			window.location.reload();
		   		} else if(localJson.exceptionInfo){
		   			var str = localJson.exceptionInfo;			   			
		   			showError(str);
		   			return;
		   		}
		   	}
		});	 
	},
	autoNewProd:function(id){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
		    url : basePath + 'pm/bom/autoNewProdECN.action',
		    params: {
		    	caller:caller,
		        id: id		       
		    },
		    method : 'post',
		    callback : function(options,success,response){
		    	me.FormUtil.setLoading(false);
		   		var localJson = new Ext.decode(response.responseText);
		   		if(localJson.success){
		   			Ext.Msg.alert("提示","自动生成料号成功！");
		   		} else if(localJson.exceptionInfo){
		   			var str = localJson.exceptionInfo;			   			
		   			showError(str);
		   			return;
		   		}
		   	}
		});	 
	},
	turnApplication:function(id){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
		    url : basePath + 'pm/bom/turnApplication.action',
		    params: {
		    	caller:caller,
		        id: id		       
		    },
		    method : 'post',
		    callback : function(options,success,response){
		    	me.FormUtil.setLoading(false);
		   		var localJson = new Ext.decode(response.responseText);
		   		if(localJson.success){
		   			showMessage("提示",localJson.data);
		   		} else if(localJson.exceptionInfo){
		   			var str = localJson.exceptionInfo;			   			
		   			showError(str);
		   			return;
		   		}
		   	}
		});	 
	}
});