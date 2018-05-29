package com.ragegamingpe.wtfenchants.common.enchantment;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.enchantment.base.ModBaseEnchantment;
import com.ragegamingpe.wtfenchants.common.helper.Config;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

public class WtfEnchantment extends ModBaseEnchantment
{
    private static Random rand = new Random();
    private static final String[] RANDOM_NAMES;
    public static final Map<Pair<Integer, String>, Consumer<EntityPlayer>> EVENTS = new HashMap<>();
    private static final Map<Consumer<EntityPlayer>, Float> WEIGHTED_EVENTS = new HashMap<>();

    static {
        RANDOM_NAMES = new String[]{"Aaden", "Aaliyah", "Aarav", "Aaron", "Abagail", "Abbey", "Abbie", "Abbigail", "Abby", "Abdiel", "Abdullah", "Abel", "Abigail", "Abigayle", "Abraham", "Abram", "Abril", "Ace", "Ada", "Adalyn", "Adalynn", "Adam", "Adan", "Addison", "Addison", "Addisyn", "Addyson", "Adelaide", "Adeline", "Adelyn", "Aden", "Adison", "Aditya", "Adolfo", "Adonis", "Adrian", "Adriana", "Adrianna", "Adriel", "Adrien", "Adrienne", "Adyson", "Aedan", "Agustin", "Ahmad", "Ahmed", "Aidan", "Aiden", "Aidyn", "Aileen", "Aimee", "Ainsley", "Aisha", "Aiyana", "Akira", "Alaina", "Alan", "Alana", "Alani", "Alanna", "Alannah", "Alayna", "Albert", "Alberto", "Alden", "Aldo", "Aleah", "Alec", "Aleena", "Alejandra", "Alejandro", "Alena", "Alessandra", "Alessandro", "Alex", "Alexa", "Alexander", "Alexandra", "Alexandria", "Alexia", "Alexis", "Alexis", "Alexus", "Alexzander", "Alfonso", "Alfred", "Alfredo", "Ali", "Ali", "Alia", "Aliana", "Alice", "Alicia", "Alijah", "Alina", "Alisa", "Alisha", "Alison", "Alissa", "Alisson", "Alivia", "Aliya", "Aliyah", "Aliza", "Allan", "Allen", "Allie", "Allison", "Allisson", "Ally", "Allyson", "Alma", "Alondra", "Alonso", "Alonzo", "Alvaro", "Alvin", "Alyson", "Alyssa", "Alyvia", "Amanda", "Amani", "Amara", "Amare", "Amari", "Amari", "Amaris", "Amaya", "Amber", "Amelia", "Amelie", "America", "Amiah", "Amina", "Amir", "Amira", "Amirah", "Amiya", "Amiyah", "Amy", "Amya", "Ana", "Anabel", "Anabella", "Anabelle", "Anahi", "Anastasia", "Anaya", "Anderson", "Andre", "Andrea", "Andreas", "Andres", "Andrew", "Andy", "Angel", "Angel", "Angela", "Angelica", "Angelina", "Angeline", "Angelique", "Angelo", "Angie", "Anika", "Aniya", "Aniyah", "Ann", "Anna", "Annabel", "Annabella", "Annabelle", "Annalise", "Anne", "Annie", "Annika", "Ansley", "Anthony", "Antoine", "Anton", "Antonio", "Antony", "Antwan", "Anya", "April", "Arabella", "Araceli", "Aracely", "Areli", "Arely", "Ari", "Aria", "Ariana", "Arianna", "Ariel", "Ariel", "Ariella", "Arielle", "Arjun", "Armando", "Armani", "Armani", "Arnav", "Aron", "Arthur", "Arturo", "Aryan", "Aryana", "Aryanna", "Asa", "Ashanti", "Asher", "Ashlee", "Ashleigh", "Ashley", "Ashly", "Ashlyn", "Ashlynn", "Ashton", "Ashtyn", "Asia", "Aspen", "Athena", "Atticus", "Aubree", "Aubrey", "Aubrie", "Audrey", "Audrina", "August", "Augustus", "Aurora", "Austin", "Autumn", "Ava", "Avah", "Averi", "Averie", "Avery", "Avery", "Axel", "Ayaan", "Ayana", "Ayanna", "Aydan", "Ayden", "Aydin", "Ayla", "Aylin", "Azaria", "Azul", "Bailee", "Bailey", "Bailey", "Barbara", "Baron", "Barrett", "Baylee", "Beatrice", "Beau", "Beckett", "Beckham", "Belen", "Belinda", "Bella", "Ben", "Benjamin", "Bennett", "Bentley", "Bernard", "Bethany", "Bianca", "Billy", "Blaine", "Blake", "Blaze", "Bo", "Bobby", "Boston", "Braden", "Bradley", "Brady", "Bradyn", "Braeden", "Braedon", "Braelyn", "Braiden", "Branden", "Brandon", "Branson", "Braxton", "Brayan", "Brayden", "Braydon", "Braylen", "Braylon", "Breanna", "Brenda", "Brendan", "Brenden", "Brendon", "Brenna", "Brennan", "Brennen", "Brent", "Brenton", "Brett", "Bria", "Brian", "Briana", "Brianna", "Brice", "Bridger", "Bridget", "Brielle", "Briley", "Brisa", "Britney", "Brittany", "Brock", "Broderick", "Brodie", "Brody", "Brogan", "Bronson", "Brooke", "Brooklyn", "Brooklynn", "Brooks", "Bruce", "Bruno", "Bryan", "Bryanna", "Bryant", "Bryce", "Brycen", "Brylee", "Brynlee", "Brynn", "Bryson", "Byron", "Cade", "Caden", "Cadence", "Cael", "Caiden", "Cailyn", "Caitlin", "Caitlyn", "Cale", "Caleb", "Cali", "Callie", "Callum", "Calvin", "Camden", "Cameron", "Cameron", "Camila", "Camilla", "Camille", "Campbell", "Camren", "Camron", "Camryn", "Camryn", "Cannon", "Cara", "Carina", "Carissa", "Carl", "Carla", "Carlee", "Carleigh", "Carley", "Carlie", "Carlo", "Carlos", "Carly", "Carmelo", "Carmen", "Carolina", "Caroline", "Carolyn", "Carsen", "Carson", "Carter", "Case", "Casey", "Casey", "Cash", "Cason", "Cassandra", "Cassidy", "Cassie", "Cassius", "Catalina", "Catherine", "Cayden", "Caylee", "Cecelia", "Cecilia", "Cedric", "Celeste", "Celia", "Cesar", "Chace", "Chad", "Chaim", "Chana", "Chance", "Chandler", "Chanel", "Charity", "Charlee", "Charles", "Charlie", "Charlie", "Charlize", "Charlotte", "Chase", "Chasity", "Chaya", "Chaz", "Chelsea", "Cherish", "Cheyanne", "Cheyenne", "Chloe", "Chris", "Christian", "Christina", "Christine", "Christopher", "Ciara", "Cierra", "Cindy", "Claire", "Clara", "Clare", "Clarence", "Clarissa", "Clark", "Claudia", "Clay", "Clayton", "Clinton", "Cloe", "Coby", "Cody", "Cohen", "Colby", "Cole", "Coleman", "Colin", "Collin", "Colt", "Colten", "Colton", "Conner", "Connor", "Conor", "Conrad", "Cooper", "Cora", "Corbin", "Cordell", "Corey", "Corinne", "Cornelius", "Cortez", "Cory", "Courtney", "Craig", "Cristal", "Cristian", "Cristina", "Cristofer", "Cristopher", "Cruz", "Crystal", "Cullen", "Curtis", "Cynthia", "Cyrus", "Dahlia", "Daisy", "Dakota", "Dakota", "Dale", "Dalia", "Dallas", "Dalton", "Damari", "Damarion", "Damaris", "Damian", "Damien", "Damion", "Damon", "Dana", "Dane", "Dangelo", "Dania", "Danica", "Daniel", "Daniela", "Daniella", "Danielle", "Danika", "Danna", "Danny", "Dante", "Daphne", "Darian", "Darien", "Dario", "Darion", "Darius", "Darnell", "Darrell", "Darren", "Darryl", "Darwin", "Dashawn", "Davian", "David", "Davin", "Davion", "Davis", "Davon", "Dawson", "Dax", "Dayami", "Dayana", "Dayanara", "Dayton", "Deacon", "Dean", "Deandre", "Deangelo", "Deanna", "Deborah", "Declan", "Deegan", "Deja", "Delaney", "Delilah", "Demarcus", "Demarion", "Demetrius", "Denise", "Denisse", "Dennis", "Denzel", "Deon", "Derek", "Dereon", "Derick", "Derrick", "Deshawn", "Desirae", "Desiree", "Desmond", "Destinee", "Destiney", "Destiny", "Devan", "Deven", "Devin", "Devon", "Devyn", "Dexter", "Diamond", "Diana", "Diego", "Dillan", "Dillon", "Dinnerbone", "Dixie", "Diya", "Dominic", "Dominick", "Dominik", "Dominique", "Dominique", "Donald", "Donavan", "Donna", "Donovan", "Donte", "Dorian", "Douglas", "Drake", "Draven", "Drew", "Dulce", "Duncan", "Dustin", "Dwayne", "Dylan", "Dylan", "Ean", "Easton", "Eddie", "Eden", "Eden", "Edgar", "Edith", "Eduardo", "Edward", "Edwin", "Efrain", "Eileen", "Elaina", "Elaine", "Eleanor", "Elena", "Eli", "Elian", "Eliana", "Elianna", "Elias", "Eliezer", "Elijah", "Elisa", "Elisabeth", "Elise", "Elisha", "Eliza", "Elizabeth", "Ella", "Elle", "Ellen", "Elliana", "Ellie", "Elliot", "Elliott", "Ellis", "Elsa", "Elsie", "Elvis", "Elyse", "Emanuel", "Emelia", "Emely", "Emerson", "Emerson", "Emery", "Emery", "Emilee", "Emilia", "Emiliano", "Emilie", "Emilio", "Emily", "Emma", "Emmalee", "Emmanuel", "Emmett", "Emmy", "Enrique", "Enzo", "Eric", "Erica", "Erick", "Erik", "Erika", "Erin", "Ernest", "Ernesto", "Esmeralda", "Esperanza", "Essence", "Esteban", "Esther", "Estrella", "Ethan", "Ethen", "Eugene", "Eva", "Evan", "Evangeline", "Eve", "Evelin", "Evelyn", "Everett", "Evie", "Ezekiel", "Ezequiel", "Ezra", "Fabian", "Faith", "Fatima", "Felicity", "Felipe", "Felix", "Fernanda", "Fernando", "Finley", "Finley", "Finn", "Finnegan", "Fiona", "Fisher", "Fletcher", "Frances", "Francesca", "Francis", "Francisco", "Franco", "Frank", "Frankie", "Franklin", "Freddy", "Frederick", "Frida", "Gabriel", "Gabriela", "Gabriella", "Gabrielle", "Gael", "Gage", "Gaige", "Garrett", "Gary", "Gauge", "Gaven", "Gavin", "Gavyn", "Gemma", "Genesis", "Genevieve", "George", "Georgia", "Geovanni", "Gerald", "Gerardo", "German", "Gia", "Giada", "Giana", "Giancarlo", "Gianna", "Gianni", "Gideon", "Gilbert", "Gilberto", "Gillian", "Gina", "Giovani", "Giovanna", "Giovanni", "Giovanny", "Giselle", "Gisselle", "Giuliana", "Glenn", "Gloria", "Gordon", "Grace", "Gracelyn", "Gracie", "Grady", "Graham", "Grant", "Grayson", "Gregory", "Greta", "Gretchen", "Greyson", "Griffin", "Guadalupe", "Guillermo", "Gunnar", "Gunner", "Gustavo", "Gwendolyn", "Hadassah", "Hadley", "Haiden", "Hailee", "Hailey", "Hailie", "Haleigh", "Haley", "Halle", "Hallie", "Hamza", "Hana", "Hanna", "Hannah", "Harley", "Harley", "Harmony", "Harold", "Harper", "Harper", "Harrison", "Harry", "Hassan", "Haven", "Hayden", "Hayden", "Haylee", "Hayley", "Haylie", "Hazel", "Heath", "Heather", "Heaven", "Hector", "Heidi", "Heidy", "Helen", "Helena", "Henry", "Hezekiah", "Hillary", "Holden", "Holly", "Hope", "Houston", "Howard", "Hudson", "Hugh", "Hugo", "Humberto", "Hunter", "Ian", "Ibrahim", "Ignacio", "Iliana", "Imani", "Immanuel", "India", "Ingrid", "Ireland", "Irene", "Iris", "Irvin", "Isaac", "Isabel", "Isabela", "Isabell", "Isabella", "Isabelle", "Isai", "Isaiah", "Isaias", "Ishaan", "Isiah", "Isis", "Isla", "Ismael", "Israel", "Issac", "Itzel", "Ivan", "Ivy", "Iyana", "Izabella", "Izabelle", "Izaiah", "Izayah", "Jabari", "Jace", "Jacey", "Jack", "Jackson", "Jacob", "Jacoby", "Jacqueline", "Jacquelyn", "Jada", "Jade", "Jaden", "Jaden", "Jadiel", "Jadon", "Jadyn", "Jadyn", "Jaeden", "Jaelyn", "Jaelynn", "Jagger", "Jaida", "Jaiden", "Jaiden", "Jaidyn", "Jaidyn", "Jaime", "Jair", "Jairo", "Jakayla", "Jake", "Jakob", "Jakobe", "Jalen", "Jaliyah", "Jamal", "Jamar", "Jamarcus", "Jamari", "Jamarion", "James", "Jameson", "Jamie", "Jamie", "Jamir", "Jamison", "Jamiya", "Jamya", "Jan", "Janae", "Jane", "Janelle", "Janessa", "Janet", "Janiah", "Janiya", "Janiyah", "Jaquan", "Jaqueline", "Jared", "Jaron", "Jarrett", "Jase", "Jasiah", "Jaslene", "Jaslyn", "Jasmin", "Jasmine", "Jason", "Jasper", "Javier", "Javion", "Javon", "Jax", "Jaxon", "Jaxson", "Jay", "Jayce", "Jaycee", "Jayda", "Jaydan", "Jayden", "Jayden", "Jaydin", "Jaydon", "Jayla", "Jaylah", "Jaylan", "Jaylee", "Jayleen", "Jaylen", "Jaylen", "Jaylene", "Jaylin", "Jaylin", "Jaylon", "Jaylyn", "Jaylynn", "Jayson", "Jayvion", "Jayvon", "Jazlene", "Jazlyn", "Jazlynn", "Jazmin", "Jazmine", "Jazmyn", "Jean", "Jefferson", "Jeffery", "Jeffrey", "Jenna", "Jennifer", "Jenny", "Jensen", "Jeramiah", "Jeremiah", "Jeremy", "Jerimiah", "Jermaine", "Jerome", "Jerry", "Jesse", "Jessica", "Jessie", "Jessie", "Jesus", "Jett", "Jewel", "Jillian", "Jimena", "Jimmy", "Joanna", "Joaquin", "Jocelyn", "Jocelynn", "Joe", "Joel", "Joey", "Johan", "Johanna", "John", "Johnathan", "Johnathon", "Johnny", "Jolie", "Jon", "Jonah", "Jonas", "Jonathan", "Jonathon", "Jordan", "Jordan", "Jorden", "Jordin", "Jordon", "Jordyn", "Jordyn", "Jorge", "Jose", "Joselyn", "Joseph", "Josephine", "Josh", "Joshua", "Josiah", "Josie", "Joslyn", "Josue", "Journey", "Jovan", "Jovani", "Jovanni", "Jovanny", "Jovany", "Joy", "Joyce", "Juan", "Judah", "Jude", "Judith", "Julia", "Julian", "Juliana", "Julianna", "Julianne", "Julie", "Julien", "Juliet", "Juliette", "Julio", "Julissa", "Julius", "June", "Junior", "Justice", "Justice", "Justin", "Justine", "Justus", "Kade", "Kaden", "Kadence", "Kadin", "Kadyn", "Kaeden", "Kael", "Kaelyn", "Kai", "Kaia", "Kaiden", "Kaila", "Kailee", "Kailey", "Kailyn", "Kaitlin", "Kaitlyn", "Kaitlynn", "Kaiya", "Kale", "Kaleb", "Kaleigh", "Kaley", "Kali", "Kaliyah", "Kallie", "Kamari", "Kamari", "Kamden", "Kameron", "Kamila", "Kamora", "Kamren", "Kamron", "Kamryn", "Kane", "Kara", "Kareem", "Karen", "Karina", "Karissa", "Karla", "Karlee", "Karley", "Karli", "Karlie", "Karly", "Karma", "Karson", "Karsyn", "Karter", "Kasen", "Kasey", "Kasey", "Kash", "Kason", "Kassandra", "Kassidy", "Kate", "Katelyn", "Katelynn", "Katherine", "Kathleen", "Kathryn", "Kathy", "Katie", "Katrina", "Kaya", "Kayden", "Kayden", "Kaydence", "Kayla", "Kaylah", "Kaylee", "Kayleigh", "Kaylen", "Kayley", "Kaylie", "Kaylin", "Kaylyn", "Kaylynn", "Keagan", "Keaton", "Keegan", "Keely", "Keenan", "Keira", "Keith", "Kellen", "Kelly", "Kelsey", "Kelsie", "Kelton", "Kelvin", "Kendal", "Kendall", "Kendall", "Kendra", "Kendrick", "Kenley", "Kenna", "Kennedi", "Kennedy", "Kenneth", "Kenny", "Kenya", "Kenyon", "Kenzie", "Keon", "Keshawn", "Kevin", "Keyla", "Keyon", "Khalil", "Khloe", "Kian", "Kiana", "Kianna", "Kiara", "Kiera", "Kieran", "Kierra", "Kiersten", "Kiley", "Killian", "Kimberly", "Kimora", "King", "Kingston", "Kinley", "Kinsley", "Kira", "Kirsten", "Kobe", "Kody", "Koen", "Kolby", "Kole", "Kolten", "Kolton", "Konner", "Konnor", "Korbin", "Krish", "Krista", "Kristen", "Kristian", "Kristin", "Kristina", "Kristopher", "Krystal", "Kyan", "Kyla", "Kylan", "Kyle", "Kylee", "Kyleigh", "Kyler", "Kylie", "Kymani", "Kyra", "Kyson", "Lacey", "Laci", "Laila", "Lailah", "Lainey", "Lamar", "Lamont", "Lana", "Lance", "Landen", "Landin", "Landon", "Landyn", "Lane", "Laney", "Lara", "Larissa", "Larry", "Laura", "Laurel", "Lauren", "Lauryn", "Lawrence", "Lawson", "Layla", "Laylah", "Layne", "Layton", "Lea", "Leah", "Leandro", "Leanna", "Lee", "Leia", "Leila", "Leilani", "Leland", "Lena", "Lennon", "Leo", "Leon", "Leonard", "Leonardo", "Leonel", "Leonidas", "Leroy", "Leslie", "Lesly", "Leticia", "Levi", "Lewis", "Lexi", "Lexie", "Leyla", "Lia", "Liam", "Liana", "Libby", "Liberty", "Lila", "Lilah", "Lilia", "Lilian", "Liliana", "Lilianna", "Lillian", "Lilliana", "Lillianna", "Lillie", "Lilly", "Lily", "Lilyana", "Lina", "Lincoln", "Linda", "Lindsay", "Lindsey", "Lisa", "Litzy", "Livia", "Lizbeth", "Lizeth", "Logan", "Logan", "Lola", "London", "London", "Londyn", "Lorelai", "Lorelei", "Lorena", "Lorenzo", "Louis", "Luca", "Lucas", "Lucia", "Lucian", "Luciana", "Luciano", "Lucille", "Lucy", "Luis", "Luka", "Lukas", "Luke", "Luna", "Luz", "Lydia", "Lyla", "Lyric", "Lyric", "Macey", "Maci", "Macie", "Mackenzie", "Macy", "Madalyn", "Madalynn", "Madden", "Maddison", "Maddox", "Madeleine", "Madeline", "Madelyn", "Madelynn", "Madilyn", "Madilynn", "Madison", "Madisyn", "Madyson", "Maeve", "Magdalena", "Maggie", "Maia", "Makai", "Makaila", "Makayla", "Makena", "Makenna", "Makenzie", "Makhi", "Malachi", "Malakai", "Malaki", "Malcolm", "Maleah", "Malia", "Malik", "Maliyah", "Mallory", "Manuel", "Mara", "Marc", "Marcel", "Marcelo", "Marco", "Marcos", "Marcus", "Mareli", "Marely", "Maren", "Margaret", "Maria", "Mariah", "Mariam", "Mariana", "Marianna", "Maribel", "Marie", "Mariela", "Marilyn", "Marin", "Marina", "Mario", "Marisa", "Marisol", "Marissa", "Maritza", "Mariyah", "Mark", "Markus", "Marlee", "Marlene", "Marley", "Marley", "Marlie", "Marlon", "Marques", "Marquis", "Marquise", "Marshall", "Martha", "Martin", "Marvin", "Mary", "Maryjane", "Mason", "Mateo", "Mathew", "Mathias", "Matias", "Matilda", "Matteo", "Matthew", "Matthias", "Mattie", "Maurice", "Mauricio", "Maverick", "Max", "Maxim", "Maximilian", "Maximillian", "Maximo", "Maximus", "Maxwell", "Maya", "Mayra", "Mckayla", "Mckenna", "Mckenzie", "Mckinley", "Meadow", "Megan", "Meghan", "Mekhi", "Melanie", "Melany", "Melina", "Melissa", "Melody", "Melvin", "Memphis", "Mercedes", "Meredith", "Messiah", "Mia", "Miah", "Micaela", "Micah", "Micah", "Michael", "Michaela", "Micheal", "Michelle", "Miguel", "Mikaela", "Mikayla", "Mike", "Mila", "Milagros", "Miles", "Miley", "Milo", "Milton", "Mina", "Mira", "Miracle", "Miranda", "Mireya", "Miriam", "Misael", "Mitchell", "Miya", "Mohamed", "Mohammad", "Mohammed", "Moises", "Mollie", "Molly", "Monica", "Monique", "Monserrat", "Morgan", "Morgan", "Moriah", "Moses", "Moshe", "Muhammad", "Mya", "Myah", "Myla", "Mylee", "Myles", "Mylie", "Nadia", "Naima", "Nancy", "Naomi", "Nash", "Nasir", "Natalee", "Natalia", "Natalie", "Nataly", "Natalya", "Natasha", "Nathalia", "Nathalie", "Nathaly", "Nathan", "Nathanael", "Nathanial", "Nathaniel", "Nathen", "Nayeli", "Nehemiah", "Neil", "Nelson", "Nevaeh", "Neveah", "Nia", "Nicholas", "Nick", "Nickolas", "Nico", "Nicolas", "Nicole", "Nigel", "Nikhil", "Niko", "Nikolai", "Nikolas", "Nina", "Noah", "Noe", "Noel", "Noelle", "Noemi", "Nola", "Nolan", "Nora", "Norah", "Nyasia", "Nyla", "Nylah", "Octavio", "Odin", "Olive", "Oliver", "Olivia", "Omar", "Omari", "Orion", "Orlando", "Oscar", "Osvaldo", "Oswaldo", "Owen", "Pablo", "Paige", "Paisley", "Paityn", "Paloma", "Pamela", "Paola", "Paris", "Parker", "Parker", "Patience", "Patricia", "Patrick", "Paul", "Paula", "Paulina", "Paxton", "Payten", "Payton", "Payton", "Pedro", "Penelope", "Perla", "Peter", "Peyton", "Peyton", "Philip", "Phillip", "Phoebe", "Phoenix", "Phoenix", "Pierce", "Pierre", "Piper", "Porter", "Pranav", "Precious", "Presley", "Preston", "Prince", "Princess", "Priscilla", "Quentin", "Quincy", "Quinn", "Quinn", "Quinten", "Quintin", "Quinton", "Rachael", "Rachel", "Raegan", "Raelynn", "Rafael", "Raiden", "Raina", "Ralph", "Ramiro", "Ramon", "Randall", "Randy", "Raphael", "Raquel", "Rashad", "Raul", "Raven", "Ray", "Rayan", "Raymond", "Rayna", "Rayne", "Reagan", "Reagan", "Rebecca", "Rebekah", "Reece", "Reed", "Reese", "Reese", "Regan", "Regina", "Reginald", "Reid", "Reilly", "Reina", "Remington", "Rene", "Renee", "Reuben", "Rex", "Rey", "Reyna", "Reynaldo", "Rhett", "Rhianna", "Rhys", "Ricardo", "Richard", "Ricky", "Rigoberto", "Rihanna", "Riley", "Riley", "Rishi", "River", "Riya", "Robert", "Roberto", "Rocco", "Roderick", "Rodney", "Rodolfo", "Rodrigo", "Rogelio", "Roger", "Rohan", "Roland", "Rolando", "Roman", "Romeo", "Ronald", "Ronan", "Ronin", "Ronnie", "Rory", "Rory", "Rosa", "Rose", "Roselyn", "Rosemary", "Ross", "Rowan", "Rowan", "Roy", "Royce", "Ruben", "Rubi", "Ruby", "Rudy", "Russell", "Ruth", "Ryan", "Ryan", "Ryann", "Ryder", "Ryker", "Rylan", "Ryland", "Rylee", "Rylee", "Ryleigh", "Rylie", "Sabrina", "Sadie", "Sage", "Sage", "Saige", "Salma", "Salvador", "Salvatore", "Sam", "Samantha", "Samara", "Samir", "Sammy", "Samson", "Samuel", "Sanaa", "Sanai", "Sandra", "Saniya", "Saniyah", "Santiago", "Santino", "Santos", "Sara", "Sarah", "Sarahi", "Sarai", "Sariah", "Sasha", "Saul", "Savanah", "Savanna", "Savannah", "Savion", "Sawyer", "Scarlet", "Scarlett", "Scott", "Seamus", "Sean", "Sebastian", "Selah", "Selena", "Selina", "Semaj", "Serena", "Serenity", "Sergio", "Seth", "Shamar", "Shane", "Shania", "Shaniya", "Shannon", "Sharon", "Shaun", "Shawn", "Shayla", "Shaylee", "Shayna", "Shea", "Shelby", "Sheldon", "Sherlyn", "Shiloh", "Shirley", "Shyann", "Shyanne", "Shyla", "Sidney", "Sidney", "Siena", "Sienna", "Sierra", "Silas", "Simeon", "Simon", "Simone", "Sincere", "Skye", "Skyla", "Skylar", "Skylar", "Skyler", "Skyler", "Slade", "Sloane", "Sofia", "Solomon", "Sonia", "Sonny", "Sophia", "Sophie", "Soren", "Spencer", "Stacy", "Stanley", "Stella", "Stephanie", "Stephany", "Stephen", "Sterling", "Steve", "Steven", "Sullivan", "Summer", "Susan", "Sydnee", "Sydney", "Sylvia", "Tabitha", "Talan", "Talia", "Taliyah", "Talon", "Tamara", "Tamia", "Tania", "Taniya", "Taniyah", "Tanner", "Tanya", "Tara", "Taryn", "Tate", "Tatiana", "Tatum", "Taylor", "Taylor", "Teagan", "Teagan", "Teresa", "Terrance", "Terrell", "Terrence", "Terry", "Tess", "Tessa", "Thaddeus", "Thalia", "Theodore", "Theresa", "Thomas", "Tia", "Tiana", "Tianna", "Tiara", "Tiffany", "Timothy", "Titus", "Tobias", "Toby", "Todd", "Tomas", "Tommy", "Tony", "Tori", "Trace", "Travis", "Trent", "Trenton", "Trevin", "Trevon", "Trevor", "Trey", "Trinity", "Tripp", "Tristan", "Tristen", "Tristian", "Tristin", "Triston", "Troy", "Trystan", "Tucker", "Turner", "Ty", "Tyler", "Tyree", "Tyrell", "Tyrese", "Tyrone", "Tyshawn", "Tyson", "Ulises", "Uriah", "Uriel", "Urijah", "Valentin", "Valentina", "Valentino", "Valeria", "Valerie", "Valery", "Van", "Vance", "Vanessa", "Vaughn", "Veronica", "Vicente", "Victor", "Victoria", "Vincent", "Violet", "Virginia", "Vivian", "Viviana", "Wade", "Walker", "Walter", "Warren", "Waylon", "Wayne", "Wendy", "Wesley", "Weston", "Whitney", "Will", "William", "Willie", "Willow", "Wilson", "Winston", "Wyatt", "Xander", "Xavier", "Ximena", "Xiomara", "Xzavier", "Yadiel", "Yadira", "Yael", "Yahir", "Yair", "Yamilet", "Yandel", "Yareli", "Yaretzi", "Yaritza", "Yasmin", "Yasmine", "Yazmin", "Yesenia", "Yosef", "Yoselin", "Yuliana", "Yurem", "Yusuf", "Zachariah", "Zachary", "Zachery", "Zack", "Zackary", "Zackery", "Zaid", "Zaiden", "Zain", "Zaire", "Zander", "Zane", "Zaniyah", "Zara", "Zaria", "Zariah", "Zavier", "Zayden", "Zayne", "Zechariah", "Zion", "Zion", "Zoe", "Zoey", "Zoie"};
    }

    public WtfEnchantment()
    {
        super("wtf", Enchantment.Rarity.RARE, EnumEnchantmentType.ARMOR);

        registerEvents();
    }

    @Override
    public void onUserHurt(EntityLivingBase user, Entity attacker, int level)
    {
        if (attacker instanceof EntityPlayer && !attacker.getEntityWorld().isRemote) {
            // Start the random trolling

            if (rand.nextFloat() <= level * 0.2) {
                float a = rand.nextFloat();
                for (Map.Entry<Consumer<EntityPlayer>, Float> event : WEIGHTED_EVENTS.entrySet()) {
                    if ((a -= event.getValue()) <= 0) {
                        event.getKey().accept((EntityPlayer) attacker);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 20 + (enchantmentLevel - 1) * 5;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(LibMisc.MOD_ID)) {
            registerEvents();
        }
    }

    private static <T> T getRandomElement(Random rand, T[] array)
    {
        return array[(int) Math.floor(rand.nextFloat() * array.length)];
    }

    private static <T> T getRandomElement(Random rand, List<T> list)
    {
        return list.get((int) Math.floor(rand.nextFloat() * list.size()));
    }

    private static void registerEvent(int weight, String name, Consumer<EntityPlayer> event)
    {
        boolean enabled = Config.getInstance().get(Config.CATEGORY_WTF, name, true).getBoolean();

        if (enabled) EVENTS.put(new MutablePair<>(weight, name), event);
        WtfEnchants.logger.info("Event " + name + ": " + (enabled ? "enabled" : "disabled"));
    }

    public static void calculateWeights()
    {
        WEIGHTED_EVENTS.clear();

        Set<Pair<Integer, String>> info = EVENTS.keySet();
        float sum = 0;
        for (Pair<Integer, String> weight : info) sum += weight.getLeft();

        for (Map.Entry<Pair<Integer, String>, Consumer<EntityPlayer>> event : EVENTS.entrySet()) {
            WEIGHTED_EVENTS.put(event.getValue(), event.getKey().getLeft() / sum);
        }

        WtfEnchants.logger.info("Calculated weighted probabilities of " + WEIGHTED_EVENTS.values().size() + " events");
    }

    public static void registerEvents()
    {
        EVENTS.clear();
        WtfEnchants.logger.info("WTF Enchant: Registering Events");

        registerEvent(Rarity.COMMON, "message", (player) -> player.sendMessage(new TextComponentString("Well... This is awkward.")));

        registerEvent(Rarity.COMMON, "dead_bush", (player) -> {
            NonNullList<ItemStack> inventory = player.inventory.mainInventory;

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack current = inventory.get(i);
                if (current == ItemStack.EMPTY) {
                    inventory.set(i, new ItemStack(Blocks.DEADBUSH, 1, 0));
                }
            }
        });

        registerEvent(Rarity.UNCOMMON, "rename", (player) -> {
            World world = player.getEntityWorld();
            List<Entity> entities = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().grow(10), entity -> entity != null && !(entity instanceof EntityPlayer) && entity instanceof EntityLiving);
            if (entities.size() > 0) {
                for (Entity target : entities) {
                    target.setCustomNameTag(getRandomElement(rand, RANDOM_NAMES));
                }
            }
        });

        registerEvent(Rarity.RARE, "party_sheep", (player) -> {
            World world = player.getEntityWorld();

            List<EntitySheep> sheeps = world.getEntitiesWithinAABB(EntitySheep.class, player.getEntityBoundingBox().grow(10));

            for (EntitySheep sheep : sheeps) {
                sheep.setFleeceColor(getRandomElement(rand, EnumDyeColor.values()));
            }
        });

        registerEvent(Rarity.RARE, "enchant", (player) -> {
            NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
            ItemStack stack;
            int maxTries = (int) Math.floor(mainInventory.size() * 1.5);
            do {
                stack = getRandomElement(rand, mainInventory);
                maxTries--;
            } while (stack == ItemStack.EMPTY && maxTries > 0);

            if (stack != ItemStack.EMPTY) {
                Collection<Enchantment> enchants = ForgeRegistries.ENCHANTMENTS.getValuesCollection();
                Enchantment enchant = getRandomElement(rand, enchants.toArray(new Enchantment[enchants.size()]));
                stack.addEnchantment(enchant, (int) (1 + Math.floor(rand.nextFloat() * enchant.getMaxLevel())));
            }
        });

        registerEvent(Rarity.RARE, "levitation", (player) -> player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 20, 0)));

        registerEvent(Rarity.VERY_RARE, "vomit", (player) -> player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 160, 40)));

        registerEvent(Rarity.VERY_RARE, "creepers", (player) -> {
            World world = player.getEntityWorld();
            for (int i = 0; i < 1 + rand.nextFloat() * 5; i++) {
                EntityCreeper creeper = new EntityCreeper(world);
                ReflectionHelper.setPrivateValue(EntityCreeper.class, creeper, 0, "explosionRadius");
                DataParameter<Boolean> poweredBool = ReflectionHelper.getPrivateValue(EntityCreeper.class, creeper, "POWERED");
                creeper.setHealth(20.0F);

                float x = (float) (player.posX - 5 + (rand.nextFloat() * 10));
                float z = (float) (player.posZ - 5 + (rand.nextFloat() * 10));
                int y = world.getHeight((int) x, (int) z);

                creeper.setLocationAndAngles(x, y, z, 0, 0);
                if (rand.nextFloat() < 0.25) creeper.getDataManager().set(poweredBool, true);
                world.spawnEntity(creeper);
            }
        });

        registerEvent(Rarity.HOLY_WHY_PLZ, "wither", (player) -> {
            World world = player.getEntityWorld();
            EntityWither wither = new EntityWither(world);

            wither.setPositionAndRotation(player.posX, player.posY, player.posZ, 0F, 0F);
            world.spawnEntity(wither);
        });

        calculateWeights();
    }

    static class Rarity
    {
        static final int COMMON = 32;
        static final int UNCOMMON = 16;
        static final int RARE = 8;
        static final int VERY_RARE = 4;
        static final int HOLY_WHY_PLZ = 1;
    }
}
