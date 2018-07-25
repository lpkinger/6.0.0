Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.BomPrice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.Inquiry','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit',
			'core.button.ResAudit','core.button.DownLoadFile','core.trigger.MultiDbfindTrigger',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.SubmitAudit','core.button.ResSubmitAudit',
      		'core.button.BOMCost','core.button.B2CBomPrice','core.button.TurnInquiry'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=bp_code]':{
    			beforerender: function(field){
    					if(Ext.getCmp('bp_code')&&Ext.getCmp('bp_code').value){
    						//field.readOnly=true;
    					}
    				},
        			delay: 200
    		},
    		'field[name=bp_bomid]': {
        			beforerender: function(field){
    					if(Ext.getCmp('bp_code')&&Ext.getCmp('bp_code').value){
    						//field.readOnly=true;
    					}
    				},
        			delay: 200
        		},
    		'erpSaveButton': {
    			click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					//me.FormUtil.beforeSave(this);
					me.beforeSave(this);
				}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('BomPrice', '新增BOM核价单', 'jsps/scm/purchase/bomPrice.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}  				
    			},
    			click: function(btn){
    				   me.FormUtil.onSubmit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bp_statuscode');
    				if(status && status.value != 'COMMITED'){
						btn.hide();
					}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpB2CBomPriceButton':{
    			afterrender: function(btn){
					if(!Ext.getCmp('bp_code')||!Ext.getCmp('bp_code').value){
						btn.hide();
    				}
    			},
    			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					id = Ext.getCmp('bp_id').value,
        					grid = Ext.getCmp('grid'),
        					bool = true;
    					if(grid){
	    					var data = grid.getStore().data;
	    					if(data.items&&data.items.length>0&&data.items[0].data.bpd_prodcode==""){
	    						showError("明细行为空，请先进行成本材料计算！");
	    						bool = false;
	    					}
    					}
    					if(bool){
    						grid.setLoading(true);
	            			Ext.Ajax.request({
	            				url: basePath + 'scm/purchase/b2cBomPrice.action',
	            				params: {
	            					caller: caller,
	            					id: id
	            				},
	            				timeout: 600000,
	            				callback: function(opt, s, r) {
	            					grid.setLoading(false);
	            					var rs = Ext.decode(r.responseText);
	            					if(rs.exceptionInfo) {
	            						showError('核价失败!'+ rs.exceptionInfo==null?"原因未知":rs.exceptionInfo);
	            					}else{
	            						alert('核价成功!');
	            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'bp_id=' + id});
	            						me.GridUtil.loadNewStore(grid, {caller: caller, condition: 'bpd_bpid=' + id});
	            					}
	            				}
	            			});
    					}
				}
    		},
    		'erpTurnInquiryButton':{
    			afterrender: function(btn){
    				btn.setWidth(120);
    				btn.setText('转商城询价');
    					if(!Ext.getCmp('bp_code')||!Ext.getCmp('bp_code').value){
    						btn.hide();
	    				}
        			},
    			click: function(btn) {
    				me.TurnB2cInquiry(btn);
    			}
    		},
    		'erpBOMCostButton': {
        			afterrender: function(btn){
        				btn.setText('材料成本计算');
        				btn.setWidth(120);
    					if(!Ext.getCmp('bp_code')||!Ext.getCmp('bp_code').value){
    						btn.hide();
	    				}
        			},
        			click: function(btn) {
        				var grid = Ext.getCmp('grid'),
        					datas = me.GridUtil.getAllGridStore(grid);
        				if(datas.length>0){
        					warnMsg("报价材料明细中已经有数据，计算将覆盖原有数据。确认计算?", function(btn){
	        					if(btn == 'yes'){
	        						me.evlBOMCost(btn);
	        					}
        					});
        				}else me.evlBOMCost(btn);
        			}
        		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 保存之前的判断
	 * @param arg 额外参数
	 */
	beforeSave: function(me, arg){
		var mm = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.FormUtil.getSeqId(form);
			}
		}
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){
					//number类型赋默认值，不然sql无法执行
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
				form.saveUrl = form.saveUrl + "?caller=" + caller;
			}
			
			me.FormUtil.save(r, [], arg);
		}else{
			me.FormUtil.checkForm();
		}
	},
	evlBOMCost:function(btn){
		var me = this,
			form = me.getForm(btn),
			id = Ext.getCmp('bp_id').value,
			grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'scm/purchase/evlBomCostPrice.action',
			params: {
				caller: caller,
				id: id
			},
			timeout: 600000,
			callback: function(opt, s, r) {
				grid.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.success) {
					alert('计算完成!');
					me.FormUtil.loadNewStore(form, {caller: caller, condition: 'bp_id=' + id});
					me.GridUtil.loadNewStore(grid, {caller: caller, condition: 'bpd_bpid=' + id});
				} else if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	},
	TurnB2cInquiry: function(btn){
		var me = this,
			form = me.getForm(btn),
			grid = Ext.getCmp('grid'),
			id = Ext.getCmp('bp_id').value,
			gridId = "",
			s = grid.getStore().data.items;
		// String caller, int id, String gridId
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
		    	if(s[i].data["bpd_turnb2cinquiry"]==true){
		    		gridId += s[i].data["bpd_id"]+",";
		    	}
			}
			if(gridId.length>1){
				gridId = gridId.substr(0,gridId.length-1);
				form.setLoading(true);
				Ext.Ajax.request({
				url: basePath + 'scm/purchase/turnB2cInquiry.action',
				params: {
					caller: caller,
					id: id,
					gridId:gridId
				},
				timeout: 600000,
				callback: function(opt, s, r) {
					form.setLoading(false);
					var rs = Ext.decode(r.responseText);
					if(rs.success) {
						if(rs.log){
							showMessage(rs.log);
						}
					} else if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					}
				}
			});
		}else{
			showError("未勾选任何明细！");
		}
	}
});