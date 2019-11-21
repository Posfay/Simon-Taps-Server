# Simon Taps Server Side with Spring

**Get state**
----
  Visszaadja a szoba és a játokos állapotát

* **URL**

  /state/{room_id}/{player_id}

* **Method:**

  `GET`
  
* **Success Response:**

  * **Case:** Game state in Waiting <br />
    * **Content:** `{ status: 'OK', game_state: [string], number_of_players: [int] }`

  * **Case:** Game state in Preparing <br />
    * **Content:** `{ status: 'OK', game_state: [string], tile_id: [int] }`
    
  * **Case:** Game state in Showing Pattern <br />
    * **Content:** `{ status: 'OK', game_state: [string], pattern: [string] }`

  * **Case:** Game state in Playing <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

  * **Case:** Game state in Successful End <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

  * **Case:** Game state in Fail End <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

* **Error Response:**
  * TODO
 
* **Example**
  
  * URL paraméterként várja a room és player id-t:
  * `GET /state/1234/1234`

**Join**
----
  Csatlakozás szobához saját player id-val

* **URL**

  /join

* **Method:**

  `POST`
  
*  **JSON Params**

   `roomId:[string]`
   
   `playerId:[string]`

* **Success Response:**

  * **Case:** Successful join <br />
    * **Content:** `{ status: 'OK', number_of_players: [int] }`

* **Error Response:**
 
  * **Reason:** Internal Server Error <br />
    * **Content:** `{ status: 'ERROR', reason: [string] }`

  * **Reason:** Room full <br />
    * **Content:** `{ status: 'ERROR', reason: [string] }`

**Start**
----
  Showing Pattern után ezen az endpointon jelez egyszer a játékos, hogy kész.

* **URL**

  /start

* **Method:**

  `POST`
  
*  **JSON Params**

   `roomId:[string]`
   
   `playerId:[string]`

* **Success Response:**

  * **Case:** Success <br />
    * **Content:** `{ status: 'OK'}`

* **Error Response:**
  * TODO

**Game**
----
  Gombot nyomott a játékos

* **URL**

  /game

* **Method:**

  `POST`
  
*  **JSON Params**

   `roomId:[string]`
   
   `playerId:[string]`

* **Success Response:**

  * **Case:** Successful press and game is still in Playing State <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

  * **Case:** Wrong Press <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

  * **Case:** Successful press and game is in Successful End State <br />
    * **Content:** `{ status: 'OK', game_state: [string] }`

* **Error Response:**
 
  * TODO


**Game States**
----

* **1_waiting**

* **2_preparing**
  
* **3_showing_pattern**

* **4_playing**
  
* **5_successful_end**
 
* **5_fail_end**


**Get State hívása**
----

* **WAITING**
  * 1000 ms-enként
* **PREPARING**
  * 250 ms-enként
* **SHOWING PATTERN**
  * 250 ms-enként
* **PLAYING**
  * 250 ms-enként