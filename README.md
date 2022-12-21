# Développement Android Laboratoire n°4 - Tâches asynchrones et Coroutines Galerie d’images

## Questions
### 3.1 
> Veuillez expliquer comment votre solution s’assure qu’une éventuelle Couroutine associée à une vue (item) de la RecyclerView soit correctement stoppée lorsque l’utilisateur scrolle dans la galerie et que la vue est recyclée.

On utilise la méthode `onViewRecycled, appelée à chaque fois qu'un élément de la recycler view est recyclé (et donc plus affiché à l'écran) pour stopper la Coroutine
de cet élément. Pour ce faire, nous stockons la Coroutine dans un job que nous arrêtons lors de l'appel à la `stopLoading` du holder. Lorsqu'un élément est recyclé, on appelle donc la méthode `stopLoading` pour stopper le téléchargement de l'image.

### 3.2 
> Comment pouvons-nous nous assurer que toutes les Coroutines soient correctement stoppées lorsque l’utilisateur quitte l’Activité ? Veuillez expliquer la solution que vous avez mis en oeuvre, est-ce la plus adaptée ?

### 3.3 
> Est-ce que l’utilisation du Dispatchers.IO est le plus adapté pour des tâches de téléchargement ? Ne faudrait-il pas plutôt utiliser un autre Dispatcher, si oui lequel ? Veuillez illustrer votre réponse en effectuant quelques tests.

### 4.1 
> Lors du lancement de la tâche ponctuelle, comment pouvons nous faire en sorte que la galerie soit raffraîchie ?

Nous appelons simplement la méthode `notifyDataSetChanged()` de l'adapter de la RecyclerView après
le vide du cache.

### 4.2 
> Comment pouvons-nous nous assurer que la tâche périodique ne soit pas enregistrée plusieurs fois ? Vous expliquerez comment la librairie WorkManager procède pour enregistrer les différentes tâches périodiques et en particulier comment celles-ci sont ré-enregistrées lorsque le téléphone est redémarré.

Afin de s'assurer que la tâche périodique ne s'enregistre pas plusieurs fois (à chaque ouverture de 
l'appli puisque nous lançons ce Work dans le onCreate), il est possible de remplacer l'utilisation
de `enqueue` par `enqueueUniquePeriodicWork`. Cela permet de s'assurer que la tâche que nous lançons
est unique et ne sera pas relancée à chaque ouverture de l'appli. Cette méthode nécessite donc un nom
unique pour la tâche et une `ExistingPeriodicWorkPolicy` en plus de la requete. Nous avons choisi
d'utilisation la politique ``ExistingPeriodicWorkPolicy.KEEP`` qui permet de garder la tâche déjà
enregistrée et de ne pas la remplacer par la nouvelle.

La réinscription des tâches périodiques après redémarrage se fait au moyen d'une base de données SQLite.
Cette dernière est gérée par la librairie WorkManager.
