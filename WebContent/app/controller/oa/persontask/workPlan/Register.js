Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workPlan.Register', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workPlan.Register','oa.persontask.workPlan.WorkPlanForm','core.form.WorkPlanField','core.form.WorkPlanField2',
    		'core.button.Save','core.button.Close','core.button.Over','core.button.Submit','core.form.PlanDate',
    		'core.button.Update','core.button.Delete','core.button.Distribute2','core.button.Transmit','core.button.File',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.DetailTextField','core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=save]':{
    			click: function(btn){
    				var mm = this.FormUtil;
    				var form = Ext.getCmp('form');
    				if(! mm.checkForm()){
    					return;
    				}
    				if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    					mm.getSeqId(form);
    					this.onSave(form);
    					this.saveDetail(form);
    				} else {
    					this.onUpdate(form);
    				}
    			}
    		},
    		'fieldset[id=wp_summary]':{
    			afterrender: function(field){
    				if(field.value=='' || field.value==null){
    	        		Ext.getCmp('save').setDisabled(true);
    	        	} else {
    	        		field.down('textareafield').setValue(field.value);
    	        	}
    			}
    		},
    		'textfield[id=wp_committime]':{
    			afterrender: function(field){
    				if(field.value=='' || field.value==null){
    	        		field.setVisible(false);
    	        	}
    			}
    		},
    		'textfield[id=wp_updatetime]':{
    			afterrender: function(field){
    				if(field.value=='' || field.value==null){
    	        		field.setVisible(false);
    	        	}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getSeqId: function(url){
		var id = 0;
		Ext.Ajax.request({
	   		url : basePath + url,
	   		method : 'get',
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				id = rs.id;
	   			}
	   		}
		});
		return id;
	},
	saveDetail: function(form){
		var mm = this.FormUtil;
		var me = this;
		if(form.getForm().isValid()){
			var detailfield = form.down('workplanfield2');
			for(var i = 1; i <= detailfield.tfnumber; i++){
				if(Ext.getCmp('wplan' + i).value != null && Ext.getCmp('wplan' + i).value != ''){
					var o = new Object();
					o.wpd_id = this.getSeqId('common/getId.action?seq=PROJECTPLANDETAIL_SEQ');
					o.wpd_wpid = Ext.getCmp(form.keyField).value;
					o.wpd_taskid = Ext.getCmp('addtask' + i).taskid;
					o.wpd_plan = Ext.getCmp('wplan' + i).value;
					o.wpd_status = 'DOING';
					var params = new Object();
					params.formStore = unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
					Ext.Ajax.request({
				   		url : basePath + 'oa/persontask/workPlan/saveWorkPlanDetail.action?caller=WorkPlanDetail',
				   		params : params,
				   		method : 'post',
				   		async: false,
				   		callback : function(options,success,response){
//				   			me.getActiveTab().setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				   			    				
				   			} else{
				   				saveFailure();//@i18n/i18n.js
				   			}
				   		}
					});
				}
			}
		}else{
			mm.checkForm();
		}
	},
	onSave: function(form){
    	var mm = this.FormUtil;
    	var me = this;
    	console.log(form);
    	if(form.getForm().isValid()){
    		Ext.getCmp('wp_committime').setValue(Ext.util.Format.date(new Date(),"Y-m-d H:i:s"));
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
			var keys = Ext.Object.getKeys(r);
			var values = Ext.Object.getValues(r);
			var o = new Object();
			Ext.each(keys, function(key, index){
				if(contains(key, 'wp_', true) && !contains(key, 'wp_time', true)){
					o[key] = values[index];
				}
			});
			o.wp_time = form.down('plandate').getValue();
			o.wp_summary = Ext.getCmp('wp_summary').value=='' ? '':Ext.getCmp('wp_summary').value;
			me.save(o, form);
		}else{
			mm.checkForm();
		}
    },
    save: function(o, form){
    	var mm = this.FormUtil;
    	var params = new Object();
		params.formStore = unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
    	Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
//	   			me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = o[form.keyField];
		   		    	var nextworkplan = form.keyField + "IS" + value ;
		   		    	if(Ext.getCmp('save').last==0){
		   		    		window.location.href = window.location.href + '?nextworkplan=' + nextworkplan;
		   		    	} else {
		   		    		window.location.href = window.location.href + '?nextworkplan=' + nextworkplan + '&lastworkplan=wp_idIS' + Ext.getCmp('save').last;
		   		    	}
    				});
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
    },
    onUpdate: function(form){
    	var mm = this.FormUtil;
    	var me = this;
		var s1 = mm.checkFormDirty(form);
		if(s1 == ''){
			showError($I18N.common.form.emptyData + '<br/>');
			return;
		}
		if(form && form.getForm().isValid()){
			Ext.getCmp('wp_updatetime').setValue(Ext.util.Format.date(new Date(),"Y-m-d H:i:s"));
			//form里面数据
			var r = form.getValues();
			var keys = Ext.Object.getKeys(r);
			var values = Ext.Object.getValues(r);
			var o = new Object();
			Ext.each(keys, function(key, index){
				if(contains(key, 'wp_', true)){
					o[key] = values[index];
				}
			});
			o.wp_summary = Ext.getCmp('wp_summary').value=='' ? '':Ext.getCmp('wp_summary').value;
			console.log(o);
			me.update(o, []);
		}else{
			mm.checkForm(form);
		}
    },
    update: function(){
    	var params = new Object();
		var r = arguments[0];
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var mm = this.FormUtil;
		var form = Ext.getCmp('form');
		mm.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			mm.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				updateSuccess(function(btn){
    					//update成功后刷新页面进入可编辑的页面 
    					var url = window.location.href;
    					var value = r[form.keyField];
    					var nextworkplan = form.keyField + "IS" + value ;
		   		    	if(mm.contains(url, 'nextworkplan', true)){
		   		    		url = url.replace('nextworkplan', 1);
		   		    	}
		   		    	if(mm.contains(url, '?', true)){
		   		    		url = url + '&nextworkplan=' + nextworkplan;
			   		    } else {
			   		    	url = url + '?nextworkplan=' + nextworkplan;
			   		    }
		   		    	window.location.href = url;
	   				});
        		} else {
	   				updateFailure();
	   			}
	   		}
		});
    }
});