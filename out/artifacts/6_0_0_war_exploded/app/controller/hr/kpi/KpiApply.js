Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.KpiApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.kpi.KpiApply','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.trigger.MultiDbfindTrigger2',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','hr.kpi.ApplyGrid','core.button.VoCreate','core.button.CheckKBIman',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger','core.window.Msg'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpApplyGrid': { 
				itemclick: this.onGridItemClick
			},
			'multidbfindtrigger[name=kad_beman]': {
				beforetrigger:function(field){
				 if(Ext.getCmp('ka_dpcode')&&Ext.getCmp('ka_dpcode').value){
				 	field.dbBaseCondition  = "em_departmentcode='" + Ext.getCmp('ka_dpcode').value+"'";
				 };
				}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ka_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKBIAssess', '新增考核申请', 'jsps/hr/kpi/kpiApply.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ka_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ka_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ka_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ka_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ka_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ka_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ka_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ka_id').value);
				}
			},			
			'erpCheckKBImanButton':{
				afterrender:function(btn){
    				btn.setDisabled(true);
    			},
				click: function(btn){
    				var id=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["kad_id"];
    				var formCondition="kad_id IS"+id;
    				var linkCaller='KBIMAN!allow';    				
    				var win = new Ext.window.Window({  
						id : 'win',
						height : '90%',
						width : '95%',
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						items : [ {
							tag : 'iframe',
							frame : true,
							anchor : '100% 100%',
							layout : 'fit',
							 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/hr/kbi/checkKBIAssess.jsp?_noc=1&whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						} ],
                        listeners:{
                          'beforeclose':function(view ,opt){
                        	   //grid  刷新一次
                        	  var grid=Ext.getCmp('grid');
                        	  var gridParam = {caller: caller, condition: gridCondition};
                        	  grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                        	  Ext.getCmp('checkKBIman').setDisabled(true);
                          }	
                        }
					});
 					win.show(); 
    			}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	onGridItemClick: function(selModel,record,me){//grid行选择
		this.GridUtil.onGridItemClick(selModel,record);
    	/*var grid=selModel.ownerCt;
    	var show=0;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    	var fieldValue=record.data[field];
           if(fieldValue==undefined||fieldValue==""||fieldValue==null){
        	   show=1;
        	   return; 
           }
        });
    	if(show==1){
        	Ext.getCmp('checkKBIman').setDisabled(true);
    	}else {
    		Ext.getCmp('checkKBIman').setDisabled(false);
		}*/
    },
    autoInsert:function(){
		var ka_detp=Ext.getCmp('ka_detp');
		if(ka_detp.value==''){
			return;
		}
		console.log(ka_detp.value);
		Ext.Ajax.request({
	   		url : basePath + 'hr/kbi/autoSaveKBIAssess.action',
	   		params: {ka_detp:ka_detp.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = localJson.ka_id;
		   		    	var formCondition = 'ka_id' + "IS" + value ;
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=kad_kaidIS'+value;
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = localJson.vr_id;
			   		    	var formCondition = 'ka_id' + "IS" + value ;
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=kad_kaidIS'+value;
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
        		} else {
        			showError(str);
	   			}
	   		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});