Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.annualPlans.Yeardising', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'crm.marketmgr.annualPlans.Merchandising','core.form.Panel','core.grid.Panel2','core.button.Scan','crm.marketmgr.annualPlans.YearDesingGrid',
		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.DeleteDetail','core.form.SeparNumber',
		'core.button.ResAudit','core.button.Flow','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
		'core.form.YnField','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.form.YearDateField'
	],
    init:function(){
    	var me = this;
        	this.control({
    		'erpYearDesingGrid': { 
    			select: this.onGridItemClick
    		
    		},
    		'gridcolumn[dataIndex=mhd_sumtotal]':{
    			 beforerender:function(column){
    			     column.xtype='numbercolumn';
    				 column.format='0,000';
    			 }
    		},
    		'gridcolumn[dataIndex=mhd_sumqty]':{
   			 beforerender:function(column){
   			     column.xtype='numbercolumn';
   				 column.format='0,000';
   			 }
   		    },
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.save(this);
    			}
    		},
    		'erpUpdateButton': {
    			 click: function(btn){	
  				   this.update(this);
  			   },
  			   afterrender:function(btn){
  				   var statuscode=Ext.getCmp('mh_statuscode').getValue();
  				   if(statuscode!='ENTERING'){
  					   btn.hide();
  				   }
  			   }
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('mh_id').value);
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mh_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.submit(Ext.getCmp('mh_id').value);
				}
			},
			'erpDeleteDetailButton':{
				afterdelete:function(selmodel,record,btn){
					   this.update(this);
				}
			 },
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mh_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mh_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mh_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mh_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mh_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mh_id').value);
				}
			},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addMerchandising', '新增年度计划', 'jsps/crm/marketmgr/annualPlans/yeardising.jsp');
    			}
    		},
    		
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	 if(!selModel.view.ownerCt.readOnly){
    		var grid=selModel.view.ownerCt;
    		var detno="mhd_detno";			
    		var index = null,arr=new Array();
			    index = record.data[detno];
				index = index == null ? (record.index + 1) : index;
				if(index.toString() == 'NaN'){
					index = '';
				}
				if(index == grid.store.last().data[detno]){//如果选择了最后一行
					for(var i=0;i < 10;i++ ){
						var o = new Object();
						o[detno] = index + i + 1;
						arr.push(o);
					}
					grid.store.loadData(arr, true);
		    	}
			    var bar=Ext.getCmp('toolbar');
				var btn = bar.down('erpDeleteDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = bar.down('erpAddDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = bar.down('copydetail');
				if(btn)
					btn.setDisabled(false);
				btn = bar.down('pastedetail');
				if(btn)
					btn.setDisabled(false);
				btn = bar.down('updetail');
				if(btn)
					btn.setDisabled(false);
				btn = bar.down('downdetail');
				if(btn)
					btn.setDisabled(false);
			} 
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save:function(grid){
	   	   var mm = this.FormUtil;
	  		var form = Ext.getCmp('form');
	  		mm.getSeqId(form);
	  		var s1 = mm.checkFormDirty(form);
	  		var s2 = '';
	  		var grid = Ext.getCmp('grid');

	  		if(grid.GridUtil){
	  			var msg = grid.checkGridDirty(grid);
	  			if(msg.length > 0){
	  				s2 = s2 + '<br/>' + grid.checkGridDirty(grid);
	  			 }
	  		  }
	  		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
	  			showError($I18N.common.form.emptyData + '<br/>' + $I18N.common.grid.emptyDetail);
	  			return;
	  		}
	  		if(form && form.getForm().isValid()){
	  			//form里面数据
	  			var r = form.getValues(false, true);
	  			//去除ignore字段
	  			var keys = Ext.Object.getKeys(r), f;
	  			Ext.each(keys, function(k){
	  				f = form.down('#' + k);
	  				if(f && f.logic == 'ignore') {
	  					delete r[k];
	  				}
	  			});
	  			if(!mm.contains(form.saveUrl, '?caller=', true)){
	  				form.saveUrl = form.saveUrl + "?caller=" + caller;
	  			}
	  			var params = [];	   			
	  				var param = grid.getGridStore();
	  				if(grid.necessaryField.length > 0 && (param == null || param == '')){
	  					warnMsg('明细表还未添加数据,是否继续?', function(btn){
	  						if(btn == 'yes'){
	  							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
	  						} else {
	  							return;
	  						}
	  					});
	  				} else {
	  					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
	  				}
	  			mm.save(r, params);
	  		}else{
	  			mm.checkForm(form);
	  		}
	      },
	  update:function(grid){
   	   var mm = this.FormUtil;
  		var form = Ext.getCmp('form');
  		var s1 = mm.checkFormDirty(form);
  		var s2 = '';
  		var grid = Ext.getCmp('grid');

  		if(grid.GridUtil){
  			var msg = grid.checkGridDirty(grid);
  			if(msg.length > 0){
  				s2 = s2 + '<br/>' + grid.checkGridDirty(grid);
  			 }
  		  }
  		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
  			showError($I18N.common.form.emptyData + '<br/>' + $I18N.common.grid.emptyDetail);
  			return;
  		}
  		if(form && form.getForm().isValid()){
  			//form里面数据
  			var r = form.getValues(false, true);
  			//去除ignore字段
  			var keys = Ext.Object.getKeys(r), f;
  			Ext.each(keys, function(k){
  				f = form.down('#' + k);
  				if(f && f.logic == 'ignore') {
  					delete r[k];
  				}
  			});
  			if(!mm.contains(form.updateUrl, '?caller=', true)){
  				form.updateUrl = form.updateUrl + "?caller=" + caller;
  			}
  			var params = [];
  			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				if(data['mhd_prodcode']=='合计')
					grid.store.removeAt(i);
			}
  				var param = grid.getGridStore();
  				if(grid.necessaryField.length > 0 && (param == null || param == '')){
  					warnMsg('明细表还未添加数据,是否继续?', function(btn){
  						if(btn == 'yes'){
  							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
  						} else {
  							return;
  						}
  					});
  				} else {
  					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
  				}

  			mm.update(r, params);
  		}else{
  			mm.checkForm(form);
  		}
      },
      onSubmit:function(id){
  		var me = this;
		var form = Ext.getCmp('form');
		var grid = Ext.getCmp('grid');	
		var s2='';
		if(form && form.getForm().isValid()){
			var s = this.FormUtil.checkFormDirty(form);	
				var param = me.GridUtil.getAllGridStore();
				if(grid.necessaryField.length > 0 && (param == null || param == '')){
					showError("明细表还未添加数据,无法提交!");
					return;
				}
				if(grid.GridUtil){
		   			var msg = grid.GridUtil.checkGridDirty(grid);
		   			if(msg.length > 0){
		   				s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
		   			 }
		   		  }
			if(s2 == '' || s2 == '<br/>'){
				me.FormUtil.submit(id);
			} else {
				Ext.MessageBox.show({
				     title:'保存修改?',
				     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
				     buttons: Ext.Msg.YESNOCANCEL,
				     icon: Ext.Msg.WARNING,
				     fn: function(btn){
				    	 if(btn == 'yes'){
				    		 me.FormUtil.onUpdate(form);
				    	 } else if(btn == 'no'){
				    		 me.FormUtil.submit(id);	
				    	 } else {
				    		 return;
				    	 }
				     }
				});
			}
		} else {
			me.FormUtil.checkForm();
		}
	}
});