Ext.QuickTips.init();
Ext.define('erp.controller.plm.cost.ProjectCostClose', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.cost.ProjectCostClose','core.grid.Panel2','core.toolbar.Toolbar','core.form.ColorField','core.form.YnField',
    		 	'core.form.MonthDateField', 'core.form.ConDateField', 
    		'core.button.Scan','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.button.VoCreate','core.button.VoCancel',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.YnColumn'
    	],
    init:function(){
       var me=this;
    	this.control({ 
    		'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'monthdatefield': {
    			afterrender: function(f) {
    				if(Ext.isEmpty(f.value)){
    					me.getCurrentYearmonth(f);
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
    		    afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pcc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectCostClose', '新增项目成本结转', 'jsps/plm/cost/projectCostClose.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pcc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pcc_id').value);
    			}
    		
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pcc_id').value);
    			}
    		},   
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('pcc_statuscode'), voucher = Ext.getCmp('pcc_vouchercode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(voucher && !Ext.isEmpty(voucher.value)){
	   					btn.hide();
	   				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pcc_id').value);
    			}
    	   },
    	   'erpVoCreateButton':{
				afterrender: function(btn){
	   				var status = Ext.getCmp('pcc_statuscode'), voucher = Ext.getCmp('pcc_vouchercode');
	   				if(status && status.value != 'AUDITED'){
	   					btn.hide();
	   				}
	   				if(voucher && !Ext.isEmpty(voucher.value)){
	   					btn.hide();
	   				}
	   			},
	   			click: function(btn){
	   				warnMsg("确定要生成凭证吗?", function(btn){
	   					if(btn == 'yes'){
	   						var id = Ext.getCmp('pcc_id').value;
	   						me.FormUtil.setLoading(true);//loading...
	   	    				Ext.Ajax.request({
	   	    			   		url : basePath + 'plm/cost/createCostVoucher.action',
	   	    			   		params: {
	   	    			   			id: id,
	   	    			   			caller: caller
	   	    			   		},
	   	    			   		method : 'post',
	   	    			   		callback : function(options,success,response){
	   	    			   			me.FormUtil.setLoading(false);
	   	    		         		var r = new Ext.decode(response.responseText);
	   	    		         		if(r.exceptionInfo){
	   	    			   				showError(r.exceptionInfo);
	   	    			   			}
	   	    		    			if(r.success){
	   	    		    				turnSuccess(function(){
	   	    		    					var id = r.id;
	   	    		    					var url = "jsps/fa/ars/voucher.jsp?formCondition=vo_id=" + id + "&gridCondition=vd_void=" + id;
	   	    		    					me.FormUtil.onAdd('Voucher' + id, '凭证' + id, url);
	   	    		    				});
	   	    		    				window.location.reload();
	   	    			   			}
	   	    		         	}
	   	    				});
	   					}
	   				});
	   			}
    	   },
    	   'erpVoUnCreateButton':{
				afterrender: function(btn){
	   				var voucher = Ext.getCmp('pcc_vouchercode');
	   				if(voucher && Ext.isEmpty(voucher.value)){
	   					btn.hide();
	   				}
	   			},
	   			click: function(btn){
	   				warnMsg("确定要取消凭证吗?", function(btn){
	   					if(btn == 'yes'){
	   						var id = Ext.getCmp('pcc_id').value;
	   						me.FormUtil.setLoading(true);//loading...
	   	    				Ext.Ajax.request({
	   	    			   		url : basePath + 'plm/cost/cancelCostVoucher.action',
	   	    			   		params: {
	   	    			   			id: id,
	   	    			   			caller: caller
	   	    			   		},
	   	    			   		method : 'post',
	   	    			   		callback : function(options,success,response){
	   	    			   			me.FormUtil.setLoading(false);
	   	    		         		var r = new Ext.decode(response.responseText);
	   	    		         		if(r.exceptionInfo){
	   	    			   				showError(r.exceptionInfo);
	   	    			   			}
	   	    		    			if(r.success){
	   	    		    				showMessage('提示', '取消凭证成功！');
	   	    		    				window.location.reload();
	   	    			   			}
	   	    		         	}
	   	    				});
	   					}
	   				});
	   			}
    	   },
    	   'dbfindtrigger[name=pcd_prjcode]': {
	   			afterrender: function(t){
	   				t.gridKey = "pcc_yearmonth";
	   				t.mappinggirdKey = "pc_yearmonth";
	   				t.gridErrorMessage = "请先选择项目！";
	   			}
	   		},
	   		'multidbfindtrigger[name=pcd_prjcode]': {
	   			afterrender: function(t){
	   				t.gridKey = "pcc_yearmonth";
	   				t.mappinggirdKey = "pc_yearmonth";
	   				t.gridErrorMessage = "请先选择项目！";
	   			}
	   		},
	   		//抓取项目
    		'button[name=catchab]':{
    			afterrender: function(btn){
	   				var status = Ext.getCmp('pcc_statuscode');
	   				if(status && status.value != 'ENTERING'){
	   					btn.disable(true);
	   				}
	   			},
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				var r = form.getValues();
    				var bars=grid.query('toolbar'),toolbar=bars[0];
    				var pcc_id = Ext.getCmp('pcc_id').value;
    				if(!pcc_id||(pcc_id&&(pcc_id == 0||pcc_id==''||pcc_id==null))){
     					Ext.Msg.alert('提示','请先保存单据');
     				}else{
     					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    					params.caller=caller;
    					//抓取
    					Ext.Ajax.request({
    				   		url : basePath + form.catchABUrl,
    				   		params : params,
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			me.FormUtil.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    			    			if(localJson.success){
    			    				catchSuccess(function(){
    			    					window.location.reload();
    			    				});
    				   			}
    			    			if(localJson.exceptionInfo){
	    			   				showError(localJson.exceptionInfo);
	    			   			}
    				   		}
    					});
     				}
    			}
    		},
    		//清除项目信息
    		'button[name=cleanab]':{
    			afterrender: function(btn){
	   				var status = Ext.getCmp('pcc_statuscode');
	   				if(status && status.value != 'ENTERING'){
	   					btn.disable(true);
	   				}
	   			},
    			click:function(btn){
    				var grid = Ext.getCmp('grid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanABUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				cleanSuccess(function(){
        			    					window.location.reload();
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				cleanFailure();//@i18n/i18n.js
        				   			}
        				   		}
        					});
    					}else{
    						return;
    					}
    				});
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
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'plm/cost/getCurrentYearmonth.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				}
			}
		});
	}
});