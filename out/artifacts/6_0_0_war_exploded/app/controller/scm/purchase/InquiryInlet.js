Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.InquiryInlet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.purchase.InquiryInletform','scm.purchase.InquiryInlet','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.button.Close',
  			'core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	me.allowAuditProcess=false;
    	this.control({ 
    		'field[name=in_purpose]':{
				beforerender: function(field){
					field.setReadOnly(false);
				}
			},
    		'field[name=in_remark]':{
				beforerender: function(field){
					field.setReadOnly(false);
				}
			},
			'multidbfindtrigger[name=ip_prodcode]':{
				afterrender: function(trigger){
					trigger._f=1;
				}
			},
    		'erpGridPanel2': {
    			afterrender: function(grid){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    				Ext.defer(function(){
    					var f = Ext.getCmp('in_id');
    					if(f && f.value > 0)
    						me.getStepWise(f.value, function(data){
    							grid.store.each(function(d){
    								var dets = [], id = d.get('id_id');
    								if(id && id > 0) {
    									Ext.Array.each(data, function(t){
    										if(t.idd_idid == id)
    											dets.push(t);
    									});
    									d.set('dets', dets);
    								}
    							});
    						});
    				}, 50);    			
    			},
    			itemclick: this.onGridItemClick
    		},
    		'field[name=in_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=in_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var grid=selModel.ownerCt;
    	var show = false;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    		var fieldValue=record.data[field];
    		if(fieldValue==undefined||fieldValue==""||fieldValue==null){
    			show = true;
    			return; 
    		}
        });
        var attach = record.data['id_attach'];
        if(attach){
        	btn = Ext.getCmp('downloadfile');
        	btn && btn.setDisabled(false);
        	btn.url = 'common/downloadbyId.action?id='+attach.substring(0,attach.indexOf(';'));
        }else{
        	btn = Ext.getCmp('downloadfile');
        	btn && btn.setDisabled(true);
        }
    	if(show){
    		var btn = Ext.getCmp('updateMaxlimitInfo');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('stepWiseInquiryAuto');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('historyquo');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('historyin');
        	btn && btn.setDisabled(true);
    	} else {
    		var btn = Ext.getCmp('updateMaxlimitInfo');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('stepWiseInquiryAuto');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('historyquo');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('historyin');
        	btn && btn.setDisabled(false);
		}    	
    	var btn = Ext.getCmp('deleteAutoDet');
    	btn && btn.setDisabled(false);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 分段询价
	 */
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form'), id = Ext.getCmp(form.keyField).value;
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.isEmpty(id) || id == 0 || id == '0'){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('id_id') == null || record.get('id_id') == 0){
					record.set('id_id', -1 * i);
				}
				var s = record.get('dets') || [];
				Ext.Array.each(s, function(t, i){
					t.idd_id = t.idd_id || 0;
					t.idd_idid = String(record.get('id_id'));
					dets.push(t);
				});
			}
		});
		me.FormUtil.beforeSave(me, Ext.encode(dets));
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(s[i].dirty){
					Ext.each(grid.columns, function(c){
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}		
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('id_id') == null || record.get('id_id') == 0){
					record.set('id_id', -1 * i);
				}
				var s = record.get('dets') || [];
				Ext.Array.each(s, function(t, i){
					t.idd_id = t.idd_id || 0;
					t.idd_idid = String(record.get('id_id'));
					dets.push(t);
				});
			}
		});
		me.FormUtil.onUpdate(me, false, null, Ext.encode(dets));
	},
	getStepWise: function(in_id, callback) {
		Ext.Ajax.request({
			url: basePath + 'scm/purchase/InquiryAuto/det.action',
			params: {
				in_id: in_id
			},
			callback: function(opt, s, r) {
				if(s) {
					var rs = Ext.decode(r.responseText);
					callback.call(null, rs);
				}
			}
		});
	},
	checkFormDirty: function(){
		var form = Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor') {
					value  = item.getValue();
				}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;

				if(Ext.typeOf(item.originalValue) != 'object'){


					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						var label = item.fieldLabel || item.ownerCt.fieldLabel ||
						item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
						if(label){
							s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
						}
					}

				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	}
});