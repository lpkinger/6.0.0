Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.feePlease2', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.fee.feePlease2','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan',
    		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  				'core.button.ResSubmit','core.button.TurnCLFBX','core.button.TurnFYBX','core.button.TurnYHFKSQ',
  				'core.button.TurnYWZDBX','core.button.End','core.button.ResEnd','core.form.FileField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn),
    					c = form.ownerCt.down('container[itemId=item2]');
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.getamount();
    				if(form.keyField){
    					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    						this.FormUtil.getSeqId(form);
    					}
    				}
    				var f = this.GridUtil.getGridStore(c.down('grid'));
    				this.FormUtil.onSave(f);
    			}
    		},
    		'combo[name=fp_type]': {
    			delay: 200,
    			afterrender : function(m) {
    				this.resetViewport(m.ownerCt, m, m.getValue());
    			},
    			change: function(m){
					this.resetViewport(m.ownerCt, m, m.getValue());
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('add' + caller, '新增费用申请', "jsps/oa/fee/feePlease2.jsp?whoami=" + caller);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.getamount();
    				var form = me.getForm(btn),
    					c = form.ownerCt.down('container[itemId=item2]'),
						f = this.GridUtil.getGridStore(c.down('grid'));
    				this.FormUtil.update(form.getValues(), f);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('fp_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);
    				
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
    			var kind=Ext.getCmp('fp_kind').value;
    			var reportName='';
    			if (kind=="借款申请单"){
    				var kindtype=Ext.getCmp('fp_type').value;
    				if(kindtype=="应酬费"){reportName="FeePleaseQt1";}
    				if(kindtype=="差旅费"){reportName="FeePleaseQt2";}
    				if(kindtype=="培训费"){reportName="FeePleaseQt3";}
    				if(kindtype=="其它"){reportName="FeePleaseQt";}
    			}else if(kind="费用报销单"){
    				reportName="FeeClaimQt";
    			}   		    
				var condition='{FeePlease.fp_id}='+Ext.getCmp('fp_id').value+'';
				var id=Ext.getCmp('fp_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value == 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onEnd(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResEnd(Ext.getCmp('fp_id').value);
    			}
    		},
    		'erpTurnCLFBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入差旅费报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/feeplease/turnCLFBX.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('fp_id').value
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
    	    		    					var url = "jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '差旅费报销单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpTurnFYBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('fp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var url = null;
    				if(caller=='FeePlease!JKSQ'){
    					url = 'oa/feeplease/jksqturnFYBX.action';
    				}
    				warnMsg("确定要转入费用报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + url,
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('fp_id').value
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
    	    		    					var url = "jsps/oa/fee/feePlease2.jsp?whoami=FeePlease!FYBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '费用报销单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
    	});
    },
    getamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(item.data['fpd_date1']!=null&&item.data['fpd_date1']!=""){
				amount= amount + Number(item.data['fpd_total']);
			}
		});
		Ext.getCmp('fp_pleaseamount').setValue(amount);
		Ext.getCmp('fp_n2').setValue(amount);
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	resetViewport : function(form, cm, val) {
		var vp = form.ownerCt.down('container[itemId=item2]');
		var c = null, a = null, b = null;
		if(caller == 'FeePlease!FYBX'){
			c = this.getCallerByType2(val),
			a = c.form, b = c.grid;
		} else {
			c = this.getCallerByType(val),
			a = c.form, b = c.grid;
		}
		vp.removeAll(true);
		if (a) {
			vp.add(Ext.create('erp.view.core.form.Panel', {
				id : 'form-oth',
				anchor: '100% 30%',
				caller: a,
				_noc: 1,
				enableTools: false
			}));
		}
		if (b) {
			var c = this.BaseUtil.getUrlParam('gridCondition');
			if (c) {
				c = c.replace(/IS/g, "=");
			}
			vp.add(Ext.create('erp.view.core.grid.Panel2', {
				caller: b,
				anchor: '100% ' + (a == null ? '100%' : '70%'),
				detno: 'fpd_detno',
				keyField: 'fpd_id',
				mainField: 'fpd_fpid',
				condition: c,
				listeners : {
					afterrender : function(grid) {
//						grid.getView().getEl().setWidth(1000);
					}
				}
			}));
		}
	},
	//借款申请单
	getCallerByType : function(t) {
		var a = null, b = null;
		switch (t) {
			case '应酬费':
				a = 'FeePlease!JKSQ!YC';
				b = 'FeePlease!JKSQ';
				break;
			case '差旅费':
				a = 'FeePlease!JKSQ!CL';
				b = 'FeePlease!JKSQ!CL';
				break;
			case '培训费':
				a = 'FeePlease!JKSQ!PX';
				b = 'FeePlease!JKSQ!PX';
				break;
			case '其它':
				b = 'FeePlease!JKSQ!QT';
				break;
		}
		return {form : a, grid : b};
	},
	//费用报销单
	getCallerByType2 : function(t) {
		var a = null, b = null;
		switch (t) {
			case '应酬费':
				a = 'FeePlease!JKSQ!YC';
				b = 'FeePlease!FYBX';
				break;
			case '差旅费':
				a = 'FeePlease!JKSQ!CL';
				b = 'FeePlease!FYBX!CL';
				break;
			case '培训费':
				a = 'FeePlease!JKSQ!PX';
				b = 'FeePlease!FYBX!PX';
				break;
			case '其它':
				b = 'FeePlease!FYBX!QT';
				break;
		}
		return {form : a, grid : b};
	}
});