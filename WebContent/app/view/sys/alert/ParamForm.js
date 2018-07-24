Ext.define('erp.view.sys.alert.ParamForm', {
	extend: 'Ext.form.Panel',
	alias: 'widget.paramform',
	/*title: '参数设置',*/
	border:false,
	frame: true,
	autoScroll: true,
	bodyPadding : 0,
	layout : 'fit',
	trackResetOnLoad:true,
	initComponent : function(){ 
		var me = this;
		me.callParent(arguments);
	},
	getFormItems: function(itemId, instanceId) {
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'sys/alert/getParamItems.action',
			params : {
				itemId: itemId,
				instanceId: instanceId
			},
			method : 'post',
			callback : function(options,success,response){
				var res = JSON.parse(response.responseText);
				var fc = [];
				Ext.Array.each(res.data, function(item) {
					var field = me.createField(item);
					fc.push(field);
				});
				if(fc.length == 0) {
					fc.push({
						xtype: 'tbtext',
						html: '<span style="color:red;">'+(Ext.getCmp('aii_itemid').value?'选中项目无参数设置':'未选中项目')+'<span>'
					});
				}
				me.resetFormItems(fc);
			}
		});
	},
	createField: function(data) {
		var _xtype = data['aa_type'],
			_con = data['aa_dbfind'],
			_fieldLabel = data['aa_desc'],
			_name = data['aa_field'],
			_items = data['aa_values'],
			_width = data['aa_width']?(data['aa_width']<=4?data['aa_width']:4):1,
			_value = data['aiv_value'],
			_readonly = data['aii_statuscode']&&data['aii_statuscode']!="ENTERING",
			fieldStyle = _readonly?'background:rgb(241, 241, 241);color: black;':'background:rgb(255, 250, 250);color:rgb(81, 81, 81);';
			
		var items = {},con;
		if(_xtype=='S') {
        	con = _con=='vague'?'包含':(_con=='novague'?'不包含':(_con=='head'?'开头是':(_con=='end'?'结尾是':(_con=='direct'?'等于':(_con=='nodirect'?'不等于':'等于')))));
        }else if(_xtype=='N') {
        	con = _con=='='?'等于':(_con=='>'?'大于':(_con=='>='?'大于等于':(_con=='<'?'小于':(_con=='<='?'小于等于':(_con=='<>'?'不等于':'等于')))));
        }else if(_xtype=='D') {
        	con = _con=='='?'等于':(_con=='>='?'开始于':(_con=='<='?'结束于':(_con=='~'?'介于':'等于')));
        }else if(_xtype=='YN' || _xtype=='C' || _xtype=='R') {
        	arr = [{display:'等于',value:'='},{display:'不等于',value:'<>'}];
        	con = _con=='='?'等于':(_con=='<>'?'不等于':'等于');
        }else if(_xtype=='CBG') {
        	con = _con=='in'?'属于':(_con=='not in'?'不属于':'属于');
        }
		if(['S', 'N', 'D', 'SQL'].indexOf(_xtype) != -1) {
			items = {
				xtype: _xtype=='S'?'textfield':(_xtype=='N'?'numberfield':(_xtype=='D'?'datefield':'textareatrigger')),
				fieldLabel: '<span>'+_fieldLabel+'</span><span style="font-size:12px;color:#515151;">('+con+')</span>',
				name: _name,
				value: _value,
				columnWidth: _width/4,
				readOnly: _readonly,
				fieldStyle: fieldStyle
			}
		}else if(_xtype == 'YN') {
			items = {
				xtype: 'combobox',
				fieldLabel: '<span>'+_fieldLabel+'</span><span style="font-size:12px;color:#515151;">('+con+')</span>',
			    store: Ext.create('Ext.data.Store', {
			    	fields: ['name', 'value'],
			    	data: [
			    		{name: '是', value: '-1'},
			    		{name: '否', value: '0'}
			    	]
			    }),
			    name: _name,
			    queryMode: 'local',
			    displayField: 'name',
			    valueField: 'value',
			    value: _value,
				columnWidth: _width/4,
				readOnly: _readonly,
				fieldStyle: fieldStyle
			}
		}else if(_xtype == 'C') {
			var sda = _items?_items.split(';'):[];
			var data = [];
			Ext.Array.each(sda, function(s) {
				var d = s.split(':');
				if(d.length == 2) {
					data.push({name: d[0], value: d[1]});
				}
			});
			items = {
				xtype: 'combobox',
				fieldLabel: '<span>'+_fieldLabel+'</span><span style="font-size:12px;color:#515151;">('+con+')</span>',
			    store: Ext.create('Ext.data.Store', {
			    	fields: ['name', 'value'],
			    	data: data
			    }),
			    name: _name,
			    queryMode: 'local',
			    displayField: 'name',
			    valueField: 'value',
			    value: _value,
				columnWidth: _width/4,
				readOnly: _readonly,
				fieldStyle: fieldStyle
			}
		}else if(_xtype == 'R' || _xtype == 'CBG') {
			var sda = _items?_items.split(';'):[];
			var v = (_value+'').replace(/[\[\] ]/g,'').split(',');
			var items = [];
			Ext.Array.each(sda, function(s, i) {
				var d = s.split(':');
				if(d.length == 2) {
					items.push({
						boxLabel: d[0],
						name: _name,
						inputValue: d[1],
						checked: _xtype=='R'?v==d[1]:v[i]==d[1],
						columnWidth: 1/sda.length,
						readOnly: _readonly
					});
				}
			});
			items = {
				xtype: _xtype=='R'?'fieldcontainer':'checkboxgroup',
				layout: 'column',
				fieldLabel: '<span>'+_fieldLabel+'</span><span style="font-size:12px;color:#515151;">('+con+')</span>',
				defaultType: _xtype=='R'?'radiofield':'checkboxfield',
				items: items,
				columnWidth: _width/4
			}
		}
		
		items.cls = 'form-field-allowBlank';
		return items;
	},
	resetFormItems: function(items) {
		var form = this;
		form.removeAll();
        Ext.Array.each(items, function(item) {
	        form.add(item);
        });
        form.doLayout();
	}
});