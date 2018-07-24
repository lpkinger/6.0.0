Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.addEquipment', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.meeting.addEquipment','core.form.Panel','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpDatalistGridPanel': {
        			afterrender: function(grid){
        				grid.onGridItemClick = function(){//改为点击button进入详细界面
        					me.onGridItemClick(grid.selModel.lastSelected);
        				};
        			}
        		},
        		'button[id=delete]': {
        			click: function(){
        				me.vastDelete();
        			}
        		},
        		'button[id=add]': {
        			click: function(){
        				me.newMeetingRoom();
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
        			afterrender: function(btn){
        				var mrid = this.getUrlParam('urlcondition').split('=')[1];
        				Ext.getCmp('eq_mrid').setValue(mrid);
        			},
        			click: function(btn){
        				parent.window.location.href = parent.window.location.href;
        				this.FormUtil.beforeClose(this);
        			}
        		},
        		'erpAddButton': {
        			click: function(btn){
        				me.FormUtil.onAdd('addEquipment', '新增设备', 'jsps/oa/meeting/addEquipment.jsp');
        			}
        		},
        		'erpUpdateButton': {
        			click: function(btn){
        				this.FormUtil.onUpdate(this);
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('eq_id').value));
        			}
        		}
        	});
        },
        getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	getUrlParam: function(name){
    		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
    	    var r=parent.window.location.search.substr(1).match(reg);   
    	    if(r!=null)   
    	    	return decodeURI(r[2]); 
    	    return null; 
    	}
});