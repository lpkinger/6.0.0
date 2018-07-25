Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkDateModel', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.attendance.EmpWorkDateModel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Print','core.button.Upload',
      		'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.Update','core.button.BeforeUpdate',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.Scan'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
            'erpGridPanel2': {
            	afterrender: function(grid){
            		grid.bbar=null;
            	},
            	itemclick: function(selModel, record) {
            		me.onGridItemClick(selModel, record);
            	}
    		},

    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var em_id = Ext.getCmp('em_id');
    				if(!em_id || em_id ==0){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpBeforeUpdateButton': {
    			afterrender: function(btn){
    				var em_id = Ext.getCmp('em_id');
    				if(!em_id || em_id ==0){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeGridUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addEmpWorkDateModel', '新增员工班次模板', 'jsps/hr/attendance/empworkdatemodel.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		
//    		'erpFormPanel' : {
//    			afterload : function(form) {
//					setTimeout(function(){
//						me.createGrid();
//					},200);
//    			}
//    		},
    		'combo[name=em_modeltype]' : {
    			delay: 200,
    			change: function(){
    				me.createGrid();
    			}

    			
    		}
    	});
    }, 
    beforeGridUpdate: function () {
    	var grid = Ext.getCmp('grid');
    	var oldStore = grid.store;
    	var type =Ext.getCmp('em_modeltype')? Ext.getCmp('em_modeltype').value:null;  //yue zhou
    	var newitems = new Array();
    	var object = new Object();
    	oldStore.each(function(item,index){
    		object[item.data.emd_detno] = item;
    		
    	});
		var week_str = ['一','二','三','四','五','六','日'];
		var count = type =='yue'?31:7;
		append = false;
		for(var i=1;i <=count;i++ ){
			var o = new Object();
			if(object.hasOwnProperty(i)){
				//原来有这个日期
				o = object[i].data;
				
			}else{
				//原来没有这个日期
				o['emd_detno'] = i ;
				o['emd_describe'] = type=='yue'?'每月'+i+'日':'星期'+week_str[i-1];
				o['emd_number'] = i;
				
			}
			newitems.push(o);
		}
		
		oldStore.loadData(newitems, append);
    },
    createGrid: function(){
		var grid = Ext.getCmp('grid');
		this.addMonthEmptyItems(grid,Ext.getCmp('em_modeltype').value,false);
    },
	addMonthEmptyItems: function(grid, type,append){
		var week_str = ['一','二','三','四','五','六','日'];
		var store = grid.store, 
			 arr = new Array();
		var detno = grid.detno;
		var count = type =='yue'?31:7;
		append = append === undefined ? true : false;
		if(detno){
			for(var i=1;i <=count;i++ ){
				var o = new Object();
				
				o[detno] = i ;
				o['emd_describe'] = type=='yue'?'每月'+i+'日':'星期'+week_str[i-1];
				o['emd_number'] = i;
				
				arr.push(o);
			}
		}
		store.loadData(arr, append);
		var i = 0;
		store.each(function(item, x){
			if(item.index) {
				i = item.index;
			} else {
				if (i) {
					item.index = i++;
				} else {
					item.index = x;
				}
			}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},

	beforeSave: function(){
		var bool = true;
		if (bool) {
		    this.FormUtil.beforeSave(this);
		}
	},
	beforeUpdate: function(){
		var bool = true;
		if (bool) {
			this.FormUtil.onUpdate(this);
		}
	},
	onGridItemClick: function(selModel, record){
		
		
		var grid = selModel.ownerCt;
		var btn = grid.down('erpDeleteDetailButton');
		if(btn)
			btn.setDisabled(false);
	}
});