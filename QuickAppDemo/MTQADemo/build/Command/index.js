(function(){
      
  var createPageHandler = function() {
    return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	var $app_template$ = __webpack_require__(4)
	var $app_style$ = __webpack_require__(5)
	var $app_script$ = __webpack_require__(6)
	
	$app_define$('@app-component/index', [], function($app_require$, $app_exports$, $app_module$){
	     $app_script$($app_module$, $app_exports$, $app_require$)
	     if ($app_exports$.__esModule && $app_exports$.default) {
	            $app_module$.exports = $app_exports$.default
	        }
	     $app_module$.exports.template = $app_template$
	     $app_module$.exports.style = $app_style$
	})
	
	$app_bootstrap$('@app-component/index',{ packagerVersion: '0.0.5'})


/***/ },
/* 1 */,
/* 2 */,
/* 3 */,
/* 4 */
/***/ function(module, exports) {

	module.exports = {
	  "type": "div",
	  "attr": {},
	  "classList": [
	    "tutorial-page"
	  ],
	  "children": [
	    {
	      "type": "div",
	      "attr": {},
	      "classList": [
	        "tutorial-row"
	      ],
	      "repeat": function () {return this.list},
	      "children": [
	        {
	          "type": "text",
	          "attr": {
	            "value": function () {return (this.$idx) + '.' + (this.$item.name)}
	          }
	        }
	      ]
	    },
	    {
	      "type": "div",
	      "attr": {},
	      "classList": [
	        "tutorial-row"
	      ],
	      "repeat": {
	        "exp": function () {return this.list},
	        "value": "value"
	      },
	      "children": [
	        {
	          "type": "text",
	          "attr": {
	            "value": function () {return (this.$idx) + '.' + (this.value.name)}
	          }
	        }
	      ]
	    },
	    {
	      "type": "div",
	      "attr": {},
	      "classList": [
	        "tutorial-row"
	      ],
	      "repeat": {
	        "exp": function () {return this.list},
	        "key": "personIndex",
	        "value": "personItem"
	      },
	      "children": [
	        {
	          "type": "text",
	          "attr": {
	            "value": function () {return (this.personIndex) + '.' + (this.personItem.name)}
	          }
	        }
	      ]
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "-------------------------------------"
	      }
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "条件指令:"
	      },
	      "classList": [
	        "if"
	      ],
	      "events": {
	        "click": "onClickCondition"
	      }
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "if: if条件"
	      },
	      "shown": function () {return this.conditionVar===1}
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "elif: elif条件"
	      },
	      "shown": function () {return (this.conditionVar===2)&&!(this.conditionVar===1)}
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "else: 其他"
	      },
	      "shown": function () {return !(this.conditionVar===1)&&!(this.conditionVar===2)}
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "-------------------------------------"
	      }
	    },
	    {
	      "type": "text",
	      "attr": {
	        "value": "点击:控制是否显示城市"
	      },
	      "events": {
	        "click": "toggleCityList"
	      }
	    },
	    {
	      "type": "list",
	      "attr": {},
	      "shown": function () {return this.showCityList===1},
	      "children": [
	        {
	          "type": "list-item",
	          "attr": {
	            "type": "city"
	          },
	          "repeat": {
	            "exp": function () {return this.cityList},
	            "value": "city"
	          },
	          "children": [
	            {
	              "type": "text",
	              "attr": {
	                "value": function () {return '城市:' + (this.city.name)}
	              }
	            },
	            {
	              "type": "div",
	              "attr": {
	                "show": function () {return this.city.showSpots}
	              },
	              "repeat": function () {return this.city.spots},
	              "children": [
	                {
	                  "type": "text",
	                  "attr": {
	                    "value": function () {return '景点:' + (this.$item.name)}
	                  }
	                }
	              ]
	            }
	          ]
	        }
	      ]
	    }
	  ]
	}

/***/ },
/* 5 */
/***/ function(module, exports) {

	module.exports = {
	  ".tutorial-page": {
	    "flexDirection": "column"
	  },
	  ".tutorial-row": {
	    "width": "85%",
	    "marginTop": "10px",
	    "marginBottom": "30px"
	  },
	  ".if": {
	    "marginTop": "20px"
	  },
	  ".tutorial-page > text": {
	    "marginTop": "20px",
	    "_meta": {
	      "ruleDef": [
	        {
	          "t": "a",
	          "n": "class",
	          "i": false,
	          "a": "element",
	          "v": "tutorial-page"
	        },
	        {
	          "t": "child"
	        },
	        {
	          "t": "t",
	          "n": "text"
	        }
	      ]
	    }
	  },
	  "list": {
	    "flexDirection": "column"
	  },
	  "list-item": {
	    "flexDirection": "column"
	  }
	}

/***/ },
/* 6 */
/***/ function(module, exports) {

	module.exports = function(module, exports, $app_require$){'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };
	
	exports.default = {
	    data: {
	        list: [{ name: 'a' }, { name: 'bb' }],
	        conditionVar: 1,
	
	        showCityList: 1,
	        cityList: [{
	            name: '北京',
	            showSopts: true,
	            spots: [{ name: '天安门' }, { name: '八达岭' }]
	        }, {
	            name: '上海',
	            showSpots: false,
	            spots: [{ name: '东方明珠' }]
	        }]
	    },
	    onInit: function onInit() {
	        this.$page.setTitleBar({ text: '指令页面' });
	    },
	    onClickCondition: function onClickCondition() {
	        this.conditionVar = ++this.conditionVar % 3;
	    },
	    toggleCityList: function toggleCityList() {
	        this.showCityList = this.showCityList === 1 ? 0 : 1;
	    }
	};
	
	
	var moduleOwn = exports.default || module.exports;
	var accessors = ['public', 'protected', 'private'];
	
	if (moduleOwn.data && accessors.some(function (acc) {
	    return moduleOwn[acc];
	})) {
	    throw new Error('页面VM对象中的属性data不可与"' + accessors.join(',') + '"同时存在，请使用private替换data名称');
	} else if (!moduleOwn.data) {
	    moduleOwn.data = {};
	    moduleOwn._descriptor = {};
	    accessors.forEach(function (acc) {
	        var accType = _typeof(moduleOwn[acc]);
	        if (accType === 'object') {
	            moduleOwn.data = Object.assign(moduleOwn.data, moduleOwn[acc]);
	            for (var name in moduleOwn[acc]) {
	                moduleOwn._descriptor[name] = { access: acc };
	            }
	        } else if (accType === 'function') {
	            console.warn('页面VM对象中的属性' + acc + '的值不能是函数，请使用对象');
	        }
	    });
	}}

/***/ }
/******/ ]);
  };
  if (typeof window === "undefined") {
    return createPageHandler();
  }
  else {
    window.createPageHandler = createPageHandler
  }
})();
//# sourceMappingURL=index.js.map