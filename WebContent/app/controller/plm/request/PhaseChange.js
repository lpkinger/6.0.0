Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.PhaseChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.request.PhaseChange','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': { 
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);   					
        			}
        		},
        		'dbfindtrigger[name=pc_prjcode]': {
        			beforetrigger:function(trigger){
        				var form = trigger.ownerCt;
        				var id = Ext.getCmp(form.keyField);
        				if(id&&id.value){
        					Ext.Msg.alert('警告','项目不允许更改，要更改请重新录一张单！');
        					return false;
        				}
        			},
        			aftertrigger:function(trigger, record, dbfinds){
        				var me=this;
        				var prj_code=record.data.prj_code;
        				me.loadPhase(prj_code);
        				var grid = Ext.getCmp('grid');
        				grid.store.removeAll();
        				me.GridUtil.add10EmptyItems(grid,40,false);
        			}
        		},
        		'dbfindtrigger[name=pcd_phase]': {
	    			afterrender:function(trigger){	
	    				trigger.autoDbfind = false;
		    			trigger.dbKey='pc_prjcode';
		    			trigger.mappingKey='prj_code';
		    			trigger.dbMessage='请先选择需要调整的项目！';
		    		}

	    		},
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('PhaseChange', '新增项目阶段变更申请单', 'jsps/plm/request/PhaseChange.jsp');
        			}
        		},        
        		'erpSaveButton': {
        			click: function(btn){
						if(me.checkTime()){
							if(me.checkPhase()){
								this.FormUtil.beforeSave(this);
							}else{
								showError('项目阶段重复，保存失败！');
							}
						}
        			}
        		},
        		'erpCloseButton': {
        			click: function(btn){
        				this.FormUtil.beforeClose(this);
        			}
        		},
        		'erpUpdateButton': {
        			click: function(btn){
						if(me.checkTime()){
							if(me.checkPhase()){
								this.FormUtil.onUpdate(this);
							}else{
								showError('项目阶段重复，更新失败！');
							}							
						}
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('pc_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
        			}
        		}		
        	});
        },
        onGridItemClick: function(selModel, record){//grid行选择
        	this.GridUtil.onGridItemClick(selModel, record); 	
        },
    	checkTime: function(){
    		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
			var bool = true;
			// 计划完成日期不能小于计划开始日期
			Ext.each(items, function(item) {
				if (item.dirty
						&& item.data[grid.necessaryField] != null
						&& item.data[grid.necessaryField] != "") {
					var ppdate = item.data['pcd_newphaseend'];
					if (Ext.Date.format(ppdate, 'Y-m-d') < Ext.Date.format(
							item.data['pcd_newphasestart'], 'Y-m-d')) {
						bool = false;
						showError('明细表第' + item.data['pcd_detno']
								+ '行的计划完成日期小于计划开始日期');
						return;
					}else if(item.data['pcd_phasestart']) {
					if (Ext.Date.format(item.data['pcd_phasestart'], 'Y-m-d')!=Ext.Date.format(item.data['pcd_newphasestart'], 'Y-m-d')&&
						Ext.Date.format(item.data['pcd_newphasestart'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {		
						bool = false;
						showError('明细表第' + item.data['pcd_detno']
								+ '行的计划开始日期小于当前日期');
						return;
					}
					}
				}
			});
			return bool;
        },
    	checkPhase: function(){
    		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
			// 项目阶段不能重复
			for(var i=0;i<items.length-1;i++){
				if(items[i].data['pcd_phaseid']&&items[i].data['pcd_phaseid']!=''){
					for(var j=i+1;j<items.length;j++){
						if(items[j].data['pcd_phaseid']&&items[j].data['pcd_phaseid']!=''){
							if(items[i].data['pcd_phaseid']==items[j].data['pcd_phaseid']){
								return false;
							}
						}
					}
				}
			}
			return true;
        },
        loadPhase:function(code){
        	var grid = Ext.getCmp('grid'),me=this;
			grid.store.removeAll();
			Ext.Ajax.request({
				method:'post',
				url:basePath+'plm/request/loadPhase.action',
				params:{
					prj_code:code
				},
				callback:function(opts,suc,res){
					var res=Ext.decode(res.responseText);
					if(res.exception){showError(res.exception);return;}
					if(res.data.length>0){
						grid.store.loadData(res.data);
					}
				}
				});
        }
});