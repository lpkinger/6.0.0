Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.MakePrepare', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.MakePrepare','core.form.Panel','core.grid.Panel2','core.button.Delete',
    		'core.button.NeedMakePrepared','core.button.Add','core.button.Save','core.button.Close',
    		'core.button.ResAudit','core.button.Update','core.button.TurnProdIOGet','core.button.ResSubmit',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn','core.button.Submit','common.datalist.Toolbar',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Audit','common.datalist.GridPanel'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					grid.readOnly = true;
    				}, 500);
    			}
    		},
    		'erpFormPanel':{
    			afterrender:function(form){   				
    				var id = Ext.getCmp("mp_id");
    				if(id && id.value != null && id.value != ''){
    					Ext.getCmp("mp_lastcode").setEditable(false);
    					Ext.getCmp("mp_mccode").setEditable(false);
    					Ext.getCmp("mp_makecode").setEditable(false);
    					Ext.getCmp("mp_linecode").setEditable(false);   					
    				}
    			}
    		},
    		
    		'#code': {
    			specialkey: function(f, e){//按ENTER自动复制到下一行
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != ''){
    						Ext.getCmp('confirm').setDisabled(false);
    						me.onConfirm();
        				}else{
        					//聚焦focus
        				}
    				}
    			}
    		},
    		'dbfindtrigger[name=mp_lastcode]':{//上一备料单根据所选的线别
    			focus: function(t){
    				if(!t.editable){
	    				t.setHideTrigger(false);
	    				t.setReadOnly(false);//用disable()可以，但enable()无效
	    				var code = Ext.getCmp("mp_linecode").value;
	    				if(code == null || code == ''){
	    					showError("请先选择维护了线别的作业单号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    				} else {
	    					t.dbBaseCondition = "mp_linecode='" + code + "'";
	    				}
    				}
    			} ,
    			afterrender:function(t){
    				var id = Ext.getCmp("mp_id");
    				if(id && id.value != null && id.value != ''){
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			}
    		}, 
    		'#mp_mccode':{
    			afterrender:function(t){
    				var id = Ext.getCmp("mp_id");
    				if(id && id.value != null && id.value != ''){
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			}
    		},
    		'#mp_makecode':{
    			afterrender:function(t){
    				var id = Ext.getCmp("mp_id");
    				if(id && id.value != null && id.value != ''){
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.beforeSave(this);   	
   			  }
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var mpid = Ext.getCmp('mp_id').value;
    				this.FormUtil.onUpdate(this);   		
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakePrepare', '新增工单备料', 'jsps/pm/mes/makePrepare.jsp');
    			}
    		},
    		'erpSubmitButton':{
    			afterrender : function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click : function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ss_id').value);
				}
			},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mp_id').value);
    			}
    		},    		
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var statuscode = Ext.getCmp('mp_statuscode'),status = Ext.getCmp("mp_status");
    				if((statuscode && statuscode.value != 'AUDITED') || (status && status.value !='已审核')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mp_id').value);
    			}
    		},
    		'erpDeleteButton':{
    			click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要删除本单据?', function(btn) {
						if(btn == 'yes') {
							me.FormUtil.onDelete(Ext.getCmp('mp_id').value);
						}
					});
    			}
    		},
    		'erpNeedMakePreparedButton':{
    			click:function(btn){
    			   var mp_id = Ext.getCmp("mp_id").value;
	     		   var win = new Ext.window.Window({ 
	     		   	      id : 'winNeedPrepared',
	     		 		  height : '90%',
	     		 		  width : '95%',
	     		 		  maximizable : true,
	     		 		  buttonAlign : 'center',
	     		 		  layout : 'anchor',
	     		 		  title:'备料清单',
	     		 		  items : [{
	     		 		      tag : 'iframe',
	     		 			  frame : true,
	     		 			  anchor : '100% 100%',
	     		 			  layout : 'fit',
	     		 			  html : '<iframe id="iframe_NeedMakePreparedWin'+mp_id+'" src="'+basePath+'jsps/common/datalist.jsp?_noc=1&whoami=MakePrepare&urlcondition=mp_id=\''+mp_id+'\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	     		 		  }]
	     		 	});
	     		 	win.show();
    			}
    		},
    		/*'dbfindtrigger[name=mp_code]': {
    			change: function(f){
    				Ext.defer(function(){
    					var maid = Ext.getCmp('mp_maid').value, mpid = Ext.getCmp('mp_id').value;
        				if(f.value != null){
        					 window.location.href = basePath + "jsps/pm/mes/makePrepare.jsp?formCondition=mp_idIS" + mpid + "&gridCondition=mp_maidIS" + maid;
        				} 	
    				}, 50);			
    			}
    		},*/
    		'erpTurnProdIOGetButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转领料单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/toProdIOGet.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('mp_id').value,
    	    			   			caller: caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!Picking&formCondition=pi_id=" + id + "&gridCondition=pd_piid=" + id;
    	    		    					me.FormUtil.onAdd('ProdInOut' + id, '领料单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
			'#confirm' : {
				click: function(btn) {
					me.onConfirm();
				},
				afterrender : function(btn){
					var statuscode = Ext.getCmp('mp_statuscode'),status = Ext.getCmp('mp_status');
    				if((statuscode && statuscode.value != 'AUDITED') || (status && status.value == '已上线')){
    					btn.setDisabled(true);
    				}					
				}
			},
			'tabpanel > #tab-list': {
				activate: function(panel) {
					var mpid = Ext.getCmp('mp_id'), condition;
					if(mpid && !Ext.isEmpty(mpid.value)){
        				condition = "mp_id=" + mpid.value;
        	    	}else{
        	    		condition = "mp_id=''";
        	    	}
					if(panel.boxReady) {
						var grid = Ext.getCmp('grid2');
	        			if(grid) {	        		
	            			grid.formCondition = condition;
	            			grid.getCount(null, grid.getCondition() || '');
	        			}
					} else {
						panel.boxReady = true;						
						panel.add({
							xtype: 'erpDatalistGridPanel',
							caller: 'MakePrepare',
							anchor: '100% 100%',
							id: 'grid2',
							formCondition: condition
						});
					}
				}
			}			
    	});
    },
	beforeSave: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
		if(! mm.FormUtil.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.FormUtil.getSeqId(form);
			}
		}
		mm.FormUtil.onSave([]);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onConfirm: function() {
		var me = this, get = Ext.getCmp('get').value, back = Ext.getCmp('back').value;
		var result = Ext.getCmp('t_result'), barcode=Ext.getCmp('code').value, mpid = Ext.getCmp('mp_id').value;
		var grid = Ext.getCmp('grid'), grid2 = Ext.getCmp('grid2');
		var statuscode = Ext.getCmp('mp_statuscode').value;
		if(Ext.isEmpty(mpid)){
			showError('请先保存备料单主表，或者指定备料单！');
			return;
		}		
		if(statuscode != 'AUDITED'){
			showError('请先审核备料单，再进行备料相关操作！');return;
		}
		if(Ext.isEmpty(barcode)){
			showError('请先采集料卷编号！');
			return;
		}
		if(get){
			me.FormUtil.getActiveTab().setLoading(true);//loading...
    			Ext.Ajax.request({
    			   	url : basePath + 'pm/mes/getBar.action',
    			   	params: {
    			   		barcode       :    barcode,
    			   		whcode        :    Ext.getCmp('mp_whcode').value,
    			   		maid          :    Ext.getCmp('mp_maid').value,
    			   		mpid          :    mpid   		   		
    			   	},
    			   	method : 'post',
    			   	callback : function(options,success,response){
    			   		me.FormUtil.getActiveTab().setLoading(false);
    			   		var r = new Ext.decode(response.responseText);
    			   		Ext.getCmp('code').setValue('');
    			   		if(r.exceptionInfo){
    			   			result.append(r.exceptionInfo, 'error');
    			   		}
    		    		if(r.success){
    		    			result.append('料卷号：' + barcode +'备料成功！站位：'+r.message.md_location);
    		    			me.GridUtil.loadNewStore(grid, {caller: caller, condition: "mp_id=" + Ext.getCmp('mp_id').value});  		    			
    			   		}
    			   	}
    			});
		} else if(back){
			me.FormUtil.getActiveTab().setLoading(true);//loading...
    		Ext.Ajax.request({
    			  url : basePath + 'pm/mes/returnBar.action',
    			  params: {
    			   	barcode: barcode,
    			   	mpid   : mpid
    			   },
    			   method : 'post',
    			   callback : function(options,success,response){
    			   		me.FormUtil.getActiveTab().setLoading(false);
    			   		var r = new Ext.decode(response.responseText);
    			   		Ext.getCmp('code').setValue('');
    			   		if(r.exceptionInfo){
    			   			result.append(r.exceptionInfo, 'error');
    			   		}
    		    		if(r.success){
    		    			result.append('料卷号：' + barcode + ',站位:'+r.message.md_location+'退回成功');
    		    			me.GridUtil.loadNewStore(grid, {caller: caller, condition: "mp_id=" +mpid});   		    			
    			   		}
    			   	}
    		 });
		}
		Ext.getCmp("code").setValue('');
	}
});