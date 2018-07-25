Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.BillTask', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'core.form.Panel', 'plm.task.BillTask', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.button.Add', 'core.button.Close', 'core.button.Over', 
			'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger','core.form.MultiField' ],
	init : function() {
		var me = this;
		this.control({
			'field[name=sourcecode]' : {
				afterrender : function(f) {
					f.setFieldStyle({
						'color' : 'red'
					});
					f.focusCls = 'mail-attach';
					var c = Ext.Function.bind(me.openSource, me);
					Ext.EventManager.on(f.inputEl, {
						mousedown : c,
						scope : f,
						buffer : 100
					});
				}
			},
			'field[name=id]' : {
    			afterrender : function(f) {
    				var id = f.getValue();
    				if (!Ext.isEmpty(id)) {
    					me.getWorkRecord(id, f.ownerCt);
    				}
    			}
    		},
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.beforeClose(this);
				}
			},
 			'erpOverButton' : {
 				click : function(b) {
 					warnMsg('确定结束该任务?', function(k){
 						if(k == 'yes' || k == 'ok') {
 							var form = b.ownerCt.ownerCt;
 		 					me.onOver(form);
 						}
 					});
 				}
 			}
		});
	},
	openSource : function(e, el, obj) {
		var f = obj.scope;
		if (f.value) {
			this.FormUtil.onAdd(null, f.ownerCt.down('#sourcecode').value,
					f.ownerCt.down('#sourcelink').value + '&_noc=1');
		}
	},
    getWorkRecord : function(id, form) {
    	var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'WorkRecord',
	   			fields: 'wr_recorder,wr_recorddate,wr_redcord',
	   			condition: 'wr_taskid=' + id + ' order by wr_recorddate'
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
	   			var status = form.down('#handstatuscode');
				if(status && status.getValue() != 'FINISHED') {
					form.down('erpOverButton').show();
	   			} else {
	   				form.down('erpOverButton').hide();
	   			}
    			if(r.success && r.data){
    				var datas = typeof r.data === 'string' ? Ext.decode(r.data) : r.data;
    				if(datas.length > 0 ) {
    					Ext.each(datas, function(){
        					form.add(me.createRecord(this.WR_RECORDER, this.WR_RECORDDATE, this.WR_REDCORD));
        				});
    				}
	   			}
	   		}
		});
    },
    createRecord : function(r, t, v) {
    	var args = {columnWidth : 1};
    	if (r) {
    		args.fieldLabel = Ext.Date.format(Ext.Date.parse(t,'Y-m-d H:i:s'),'m-d H:i:s') + '<br>' + r;
    		args.value = v;
    		args.readOnly = true;
    		args.fieldStyle = 'background:#f1f1f1;';
    		args.labelSeparator = '';
    	} else {
    		args.fieldLabel = '处理情况描述';
    		args.name = 'wr_redcord';
    		args.id = 'wr_redcord';
    		args.cls = 'form-field-allowBlank';
    	}
    	return Ext.create('Ext.form.field.TextArea', args);
    },
    onOver : function(form) {
    	var id = form.down('#id').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/endBillTask.action',
    		params : {
    			caller : caller,
    			_noc : 1,
    			id : id
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '结束成功!', 1000);
    				window.location.reload();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    }
});