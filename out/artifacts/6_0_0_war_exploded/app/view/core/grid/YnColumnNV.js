/**
 * 自定义grid.column(无默认值)
 * yn即yes/no,显示为是和否,其实际值对应-1和0
 */
Ext.define('erp.view.core.grid.YnColumnNV', {
    extend: 'Ext.grid.column.Column',
    alias: ['widget.ynnvcolumn'],
    trueText: $I18N.common.form.yes,
    falseText: $I18N.common.form.no,
    constructor: function(cfg){
        this.callParent(arguments);
        if(!this.readOnly){
            this.editor = {
                	xtype: 'combo',
                	store: Ext.create('Ext.data.Store', {
                        fields: ['display', 'value'],
                        data : [
                            {"display": $I18N.common.form.yes, "value": '-1'},
                            {"display": $I18N.common.form.no, "value": '0'}
                        ]
                    }),
                    editable: false,
                    displayField: 'display',
                    valueField: 'value',
            		queryMode: 'local',
            		hideTrigger: false,
            		value: null,
            		listeners: {
            			scope: this,
            			'change': function(c){
            				if(c.rawValue != this.trueText && c.rawValue != this.falseText){
            					//实现grid单元格编辑模式下，不让用户编辑combo
            					if(contains(c.rawValue, this.falseText, true)){
                					c.setValue(this.falseText);
                				} else if(contains(c.rawValue, this.trueText, true)){
                					c.setValue(this.trueText);
                				} else {
                					c.setValue(this.falseText);
                				}
            				}
            			}
            		}
            };
        }
        var trueText = this.trueText,
            falseText = this.falseText, 
            necessary = this.logic == 'necessaryField';
        this.renderer = function(value){
        	var val = '';
        	if(value == '-1' || value == '1'){
        		val = trueText;
            } else if(value == '0') {
            	val = falseText;
            }
        	necessary && val == '' && (val = '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
        	'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>');
            return val;
        };
    }
});
//Ext.data.Types.YNNV = {
//		convert : function(v, data) {
//			return v;
//		},
//		sortType : function(v) {
//			return v.Latitude;
//		},
//		type : 'ynnv'
//};